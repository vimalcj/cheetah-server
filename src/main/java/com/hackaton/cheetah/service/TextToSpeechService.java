package com.hackaton.cheetah.service;

import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.repository.EmployeeRepository;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.PullAudioOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;

@Service
@Slf4j
public class TextToSpeechService {

    @Autowired
    EmployeeRepository employeeRepository;

    private static final String SubscriptionKey = "9854d01d417c4d99bd730d7aa799478e";

    private static final String ServiceRegion = "eastus";

    // Speech synthesis to MP3 file.
    public Employee synthesisToMp3FileAsync(Employee employee) throws Exception {
        try {
            SpeechConfig config = SpeechConfig.fromSubscription(SubscriptionKey, ServiceRegion);

            // https://docs.microsoft.com/azure/cognitive-services/speech-service/language-support
            if (!ObjectUtils.isEmpty(employee.getCountry()))
                config.setSpeechSynthesisLanguage(employee.getCountry());

            config.setSpeechSynthesisVoiceName("en-IN-PrabhatNeural");//English Indian Male Voice

            PullAudioOutputStream stream = PullAudioOutputStream.create();
            config.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio16Khz32KBitRateMonoMp3);
            String fileName = employee.getEmpName() + "-" + employee.getEmpId() + ".mp3";

            AudioConfig streamOutput = AudioConfig.fromStreamOutput(stream);
            SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, streamOutput);

            String text = employee.getEmpName();
            SpeechSynthesisResult result = synthesizer.SpeakTextAsync(text).get();
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                log.info("Speech synthesized for text [" + text + "], and the audio was saved to [" + fileName + "]");
                byte[] bytes = result.getAudioData();
                String upLoadPath = uploadFileToCloud(fileName, bytes);
                //upLoadPath = upLoadPath + signaturePolicy;
                employee.setRecordUrl(upLoadPath);
                employee.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                employee.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                employee = employeeRepository.save(employee);
            } else if (result.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
                log.info("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error) {
                    log.info("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    log.info("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                    log.info("CANCELED: Did you update the subscription info?");
                }

                result.close();
                throw new Exception("error while generating audio from text reason: " + result.getReason());
            }

            synthesizer.close();
            streamOutput.close();
            return employee;
        } catch (Exception e) {
            log.error("error in synthesisToMp3FileAsync function", e);
            throw e;
        }

    }

    public String uploadFileToCloud(String fileName, byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        String filepath = "https://sqlvahil7754uizsa4.file.core.windows.net/emp-pronounce/test/";
        try {
            ShareDirectoryClient dirClient = new ShareFileClientBuilder()
                    .connectionString("DefaultEndpointsProtocol=https;AccountName=sqlvahil7754uizsa4;AccountKey=R6fRuYbXcdDo9TC6pXb86b4nwnvNnWNhPgIDdTSFrUITRpnnRdr6XeFFeyUNlg4kni8PcdFtjwnT+AStF0X0Gg==;BlobEndpoint=https://sqlvahil7754uizsa4.blob.core.windows.net/;QueueEndpoint=https://sqlvahil7754uizsa4.queue.core.windows.net/;TableEndpoint=https://sqlvahil7754uizsa4.table.core.windows.net/;FileEndpoint=https://sqlvahil7754uizsa4.file.core.windows.net/;").shareName("emp-pronounce")
                    .resourcePath("test")
                    .buildDirectoryClient();

            log.info("uploadFile fileName: " + fileName);
            ShareFileClient fileClient = dirClient.createFile(fileName, bytes.length);
            fileClient.upload(bis, bytes.length);
            return filepath + fileName;
        } catch (Exception e) {
            log.error("uploadFile exception: " + e.getMessage());
            return "";
        }
    }


    public Employee updateExistingVoiceFile(byte[] bytes, Employee employee, String fileType) {
        String fileName = employee.getEmpName() + "-" + employee.getEmpId() + "." + fileType;
        String upLoadPath = uploadFileToCloud(fileName, bytes);
        //upLoadPath = upLoadPath + signaturePolicy;
        employee.setRecordUrl(upLoadPath);
        employee.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        return employeeRepository.save(employee);
    }

}

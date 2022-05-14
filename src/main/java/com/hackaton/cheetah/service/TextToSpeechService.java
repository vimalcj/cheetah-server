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
import java.io.IOException;
import java.sql.Timestamp;

@Service
@Slf4j
public class TextToSpeechService {

    @Autowired
    EmployeeRepository employeeRepository;

    private static final String SubscriptionKey = "9854d01d417c4d99bd730d7aa799478e";

    private static final String ServiceRegion = "eastus";

    private final String signaturePolicy = "?sv=2020-10-02&ss=btqf&srt=sco&st=2022-05-14T11%3A37%3A43Z&se=2022-05-15T11%3A37%3A43Z&sp=rwdxlcup&sig=qcyzPuJoo%2BQIxj7SnrRTJANocvqyc6MTb6lVGw1kvj0%3D";

    // Speech synthesis to MP3 file.
    public void synthesisToMp3FileAsync(Employee employee) {
        try {
            SpeechConfig config = SpeechConfig.fromSubscription(SubscriptionKey, ServiceRegion);

            // https://docs.microsoft.com/azure/cognitive-services/speech-service/language-support
            if (!ObjectUtils.isEmpty(employee.getCountry()))
                config.setSpeechSynthesisLanguage(employee.getCountry());

            PullAudioOutputStream stream = PullAudioOutputStream.create();

            config.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio16Khz32KBitRateMonoMp3);
            String fileName = employee.getEmpName() + "-" + employee.getEmpId() + ".mp3";


            //AudioConfig fileOutput = AudioConfig.fromWavFileOutput(fileName);
            AudioConfig streamOutput = AudioConfig.fromStreamOutput(stream);

            // Creates a speech synthesizer using an mp3 file as audio output.
            SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, streamOutput);

            String text = employee.getEmpName();
            SpeechSynthesisResult result = synthesizer.SpeakTextAsync(text).get();
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                log.info("Speech synthesized for text [" + text + "], and the audio was saved to [" + fileName + "]");
                //   result.getAudioData();
                byte[] bytes = result.getAudioData();
                // Files.write(path, bytes);
                String upLoadPath = uploadFileToCloud(fileName, bytes);
                upLoadPath = upLoadPath + signaturePolicy;
                employee.setRecordUrl(upLoadPath);
                employee.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                employee.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                employeeRepository.save(employee);
            } else if (result.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
                log.info("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error) {
                    log.info("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    log.info("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                    log.info("CANCELED: Did you update the subscription info?");
                }

                result.close();
            }

            synthesizer.close();
            //  fileOutput.close()
            //Files.delete(path)
            streamOutput.close();
        } catch (Exception e) {
            log.error("error in synthesisToMp3FileAsync function", e);
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

            log.info("uploadFile fileNmae: " + fileName);
            //ShareFileClient fileClient = dirClient.getFileClient(fileName);

            ShareFileClient fileClient = dirClient.createFile(fileName, bytes.length);

            //fileClient.create(1024000);
            fileClient.upload(bis, bytes.length);
            return filepath + fileName;
        } catch (Exception e) {
            log.error("uploadFile exception: " + e.getMessage());
            return "";
        }
    }


    public Employee updateExistingVoiceFile(byte[] bytes, Employee employee) {
        String fileName = employee.getEmpName() + "-" + employee.getEmpId() + ".mp3";
        String upLoadPath = uploadFileToCloud(fileName, bytes);
        upLoadPath = upLoadPath + signaturePolicy;
        employee.setRecordUrl(upLoadPath);
        employee.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        employeeRepository.save(employee);
        return employee;
    }

}

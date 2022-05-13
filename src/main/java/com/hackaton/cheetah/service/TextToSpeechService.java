package com.hackaton.cheetah.service;

import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.repository.EmployeeRepository;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

@Service
public class TextToSpeechService {

    @Autowired
    EmployeeRepository employeeRepository;

    private static String SubscriptionKey = "9854d01d417c4d99bd730d7aa799478e";

    private static String ServiceRegion = "eastus";

    // Speech synthesis to MP3 file.
    public void synthesisToMp3FileAsync(Employee employee) throws InterruptedException, ExecutionException, IOException
    {
        SpeechConfig config = SpeechConfig.fromSubscription(SubscriptionKey, ServiceRegion);
        config.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio16Khz32KBitRateMonoMp3);
        String fileName = employee.getEmpName()+"-"+employee.getEmpId()+".mp3";
        AudioConfig fileOutput = AudioConfig.fromWavFileOutput(fileName);
        Path path = null;
        // Creates a speech synthesizer using an mp3 file as audio output.
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, fileOutput);

            String text = employee.getEmpName();
            SpeechSynthesisResult result = synthesizer.SpeakTextAsync(text).get();
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Speech synthesized for text [" + text + "], and the audio was saved to [" + fileName + "]");
                result.getAudioData();
                byte[] bytes = result.getAudioData();
                path = Paths.get(fileName);
                Files.write(path, bytes);
                String upLoadPath = uploadFileToCloud(path.toFile().getAbsolutePath(),fileName);
                employee.setRecordUrl(upLoadPath);
                employeeRepository.save(employee);
            }
            else if (result.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
                System.out.println("CANCELED: Reason=" + cancellation.getReason());

                if (cancellation.getReason() == CancellationReason.Error) {
                    System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                    System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                    System.out.println("CANCELED: Did you update the subscription info?");
                }

                result.close();
            }


        synthesizer.close();
        fileOutput.close();
        Files.delete(path);

    }


    public String uploadFileToCloud( String path,String fileName)
    {
        String filepath ="https://sqlvahil7754uizsa4.file.core.windows.net/emp-pronounce/test/";
        try
        {
            ShareDirectoryClient dirClient = new ShareFileClientBuilder()
                    .connectionString("DefaultEndpointsProtocol=https;AccountName=sqlvahil7754uizsa4;AccountKey=R6fRuYbXcdDo9TC6pXb86b4nwnvNnWNhPgIDdTSFrUITRpnnRdr6XeFFeyUNlg4kni8PcdFtjwnT+AStF0X0Gg==;BlobEndpoint=https://sqlvahil7754uizsa4.blob.core.windows.net/;QueueEndpoint=https://sqlvahil7754uizsa4.queue.core.windows.net/;TableEndpoint=https://sqlvahil7754uizsa4.table.core.windows.net/;FileEndpoint=https://sqlvahil7754uizsa4.file.core.windows.net/;").shareName("emp-pronounce")
                    .resourcePath("test")
                    .buildDirectoryClient();

            System.out.println("uploadFile fileNmae: " + fileName);
            ShareFileClient fileClient = dirClient.getFileClient(fileName);
            fileClient.create(1024000);
            fileClient.uploadFromFile(fileName);
            return filepath+fileName;
        }
        catch (Exception e)
        {
            System.out.println("uploadFile exception: " + e.getMessage());
            return "";
        }
    }


    public Employee updateExistingVoiceFile(byte[] bytes ,String empName,Long empId) throws  IOException{
        String fileName = empName+"-"+empId+".mp3";
        Path path = Paths.get(fileName);
        Files.write(path, bytes);
        String upLoadPath = uploadFileToCloud(path.toFile().getAbsolutePath(),fileName);
        Employee employee = new Employee(empName,empId,upLoadPath);
       // employee.setRecordUrl(upLoadPath);
        employeeRepository.save(employee);
        Files.delete(path);
        return employee;
    }

}

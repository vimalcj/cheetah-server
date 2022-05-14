package com.hackaton.cheetah.service;

import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import com.azure.storage.file.share.ShareServiceVersion;
import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.repository.EmployeeRepository;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioOutputStream;
import com.microsoft.cognitiveservices.speech.audio.PullAudioOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class TextToSpeechService {

    @Autowired
    EmployeeRepository employeeRepository;

    private static String SubscriptionKey = "9854d01d417c4d99bd730d7aa799478e";

    private static String ServiceRegion = "eastus";

    private String signaturePolicy ="?sv=2020-10-02&ss=btqf&srt=sco&st=2022-05-14T11%3A37%3A43Z&se=2022-05-15T11%3A37%3A43Z&sp=rwdxlcup&sig=qcyzPuJoo%2BQIxj7SnrRTJANocvqyc6MTb6lVGw1kvj0%3D";

    // Speech synthesis to MP3 file.
    public void synthesisToMp3FileAsync(Employee employee) throws InterruptedException, ExecutionException, IOException
    {
        SpeechConfig config = SpeechConfig.fromSubscription(SubscriptionKey, ServiceRegion);
        // Sets the synthesis language.
        // The full list of supported language can be found here:
        // https://docs.microsoft.com/azure/cognitive-services/speech-service/language-support
        /*String language = "de-DE";*/

        if(!StringUtils.isEmpty(employee.getCountry()))
          config.setSpeechSynthesisLanguage(employee.getCountry());


        PullAudioOutputStream stream = PullAudioOutputStream.create();

        config.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio16Khz32KBitRateMonoMp3);
        String fileName = employee.getEmpName()+"-"+employee.getEmpId()+".mp3";


         //AudioConfig fileOutput = AudioConfig.fromWavFileOutput(fileName);
        AudioConfig streamOutput = AudioConfig.fromStreamOutput(stream);

        Path path = null;

        // Creates a speech synthesizer using an mp3 file as audio output.
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, streamOutput);

            String text = employee.getEmpName();
            SpeechSynthesisResult result = synthesizer.SpeakTextAsync(text).get();
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                System.out.println("Speech synthesized for text [" + text + "], and the audio was saved to [" + fileName + "]");
             //   result.getAudioData();
                byte[] bytes = result.getAudioData();
                path = Paths.get(fileName);
               // Files.write(path, bytes);
                String upLoadPath = uploadFileToCloud(fileName,bytes);
                upLoadPath = upLoadPath +signaturePolicy;
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
      //  fileOutput.close();
        //Files.delete(path);
        streamOutput.close();;

    }


  /*  public String uploadFileToCloud( String path,String fileName)
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(source);
        String filepath ="https://sqlvahil7754uizsa4.file.core.windows.net/emp-pronounce/test/";
        try
        {
            ShareDirectoryClient dirClient = new ShareFileClientBuilder()
                    .connectionString("DefaultEndpointsProtocol=https;AccountName=sqlvahil7754uizsa4;AccountKey=R6fRuYbXcdDo9TC6pXb86b4nwnvNnWNhPgIDdTSFrUITRpnnRdr6XeFFeyUNlg4kni8PcdFtjwnT+AStF0X0Gg==;BlobEndpoint=https://sqlvahil7754uizsa4.blob.core.windows.net/;QueueEndpoint=https://sqlvahil7754uizsa4.queue.core.windows.net/;TableEndpoint=https://sqlvahil7754uizsa4.table.core.windows.net/;FileEndpoint=https://sqlvahil7754uizsa4.file.core.windows.net/;").shareName("emp-pronounce")
                    .resourcePath("test")
                    .buildDirectoryClient();

            System.out.println("uploadFile fileNmae: " + fileName);
            ShareFileClient fileClient = dirClient.getFileClient(fileName);
            //fileClient.create(1024000);
            fileClient.uploadFromFile(fileName);
            return filepath+fileName;
        }
        catch (Exception e)
        {
            System.out.println("uploadFile exception: " + e.getMessage());
            return "";
        }
    }
*/

    public String uploadFileToCloud(String fileName,byte[] bytes )
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        String filepath ="https://sqlvahil7754uizsa4.file.core.windows.net/emp-pronounce/test/";
        try
        {
            ShareDirectoryClient dirClient = new ShareFileClientBuilder()
                    .connectionString("DefaultEndpointsProtocol=https;AccountName=sqlvahil7754uizsa4;AccountKey=R6fRuYbXcdDo9TC6pXb86b4nwnvNnWNhPgIDdTSFrUITRpnnRdr6XeFFeyUNlg4kni8PcdFtjwnT+AStF0X0Gg==;BlobEndpoint=https://sqlvahil7754uizsa4.blob.core.windows.net/;QueueEndpoint=https://sqlvahil7754uizsa4.queue.core.windows.net/;TableEndpoint=https://sqlvahil7754uizsa4.table.core.windows.net/;FileEndpoint=https://sqlvahil7754uizsa4.file.core.windows.net/;").shareName("emp-pronounce")
                    .resourcePath("test")
                    .buildDirectoryClient();

            System.out.println("uploadFile fileNmae: " + fileName);
            //ShareFileClient fileClient = dirClient.getFileClient(fileName);


            ShareFileClient fileClient = dirClient.createFile(fileName,bytes.length);

            //fileClient.create(1024000);
            fileClient.upload(bis,bytes.length);
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
       // String upLoadPath = uploadFileToCloud(path.toFile().getAbsolutePath(),fileName);
        String upLoadPath = uploadFileToCloud(fileName,bytes);
        upLoadPath = upLoadPath +signaturePolicy;
        Employee employee = new Employee(empId,empName,false,true,upLoadPath);
       // employee.setRecordUrl(upLoadPath);
        employeeRepository.save(employee);
        Files.delete(path);
        return employee;
    }

}

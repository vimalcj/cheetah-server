package com.hackaton.cheetah.converter;

import com.hackaton.cheetah.model.Employee;
import com.hackaton.cheetah.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConverterUtil {

    @Value("${azure.storage.signature}")
    private String signaturePolicy; //= "?sv=2021-04-10&st=2022-05-15T12%3A04%3A33Z&se=2022-06-24T12%3A04%3A00Z&sr=s&sp=rl&sig=os3D0JS8N428uhgH1VKJDK%2FdtRah5Fx2O12ALGfBivM%3D";


    public User convertToUser(Employee employee) {
        String sign = signaturePolicy + "&tmpId=" + java.util.UUID.randomUUID();
        return User.builder().userId(employee.getUID())
                .empId(employee.getEmpId())
                .name(employee.getEmpName())
                .admin(employee.getIsAdmin())
                .email(employee.getEmail())
                .imageUrl(ObjectUtils.isEmpty(employee.getImageUrl()) ? "" : employee.getImageUrl().concat(sign))
                .recordUrl((ObjectUtils.isEmpty(employee.getRecordUrl()) ? "" : employee.getRecordUrl().concat(sign)))
                .createdTs(employee.getCreatedDate())
                .modifiedTs(employee.getUpdatedDate())
                .build();
    }

    public List<User> convertToUser(List<Employee> employees) {
        return employees.stream().map(this::convertToUser).collect(Collectors.toList());
    }


    public void setSignaturePolicy(String signaturePolicy) {
        this.signaturePolicy = signaturePolicy;
    }
}

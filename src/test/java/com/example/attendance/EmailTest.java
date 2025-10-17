package com.example.attendance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ams.attendance.service.EmailService;

@SpringBootTest
public class EmailTest {
    @Autowired
    private EmailService emailService;

    public void TestEamil(){

        emailService.sendEmails("shane123doe@gmail.com","Test My Java App","Loolzzz is working");

    }
    
}

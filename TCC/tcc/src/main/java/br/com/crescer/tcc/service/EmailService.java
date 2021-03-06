/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.crescer.tcc.service;


import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
/**
 *
 * @author luan.avila
 */
@Service
@RequiredArgsConstructor
public class EmailService {
    
    public String grupo = "- Você foi convidado para um novo grupo no Bom De Bola. Cadastre-se ou faça seu login marcar uma partida com seu amigo(a)s";
    public String partida = "Você foi convidado para uma nova partida no Bom De Bola. Faça seu login agora para confirmar";
    
    public void enviarEmail(String email, String mensagem){
        Properties props = new Properties();
        /** Parâmetros de conexão com servidor Gmail */
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication("cwibomdebola@gmail.com", "BomDeBola");
                }
            });
            try{
                  Message message = new MimeMessage(session);
                  message.setFrom(new InternetAddress("cwibomdebola@gmail.com")); //Remetente

                  //Destinatário(s)
                  Address[] toUser = InternetAddress.parse(email);
                  message.setRecipients(Message.RecipientType.TO, toUser);
                  message.setSubject("Bom de Bola - convite pra você");
                  message.setText(mensagem);
                  /**Método para enviar a mensagem criada*/
                  Transport.send(message);
                } catch (MessagingException e) {
                  throw new RuntimeException(e);
                }
    }   
}
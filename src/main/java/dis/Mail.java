package dis;

import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail {
	private static Properties prop = null;
	private final static String username = "username";
	private final static String password = "password";
	private final static String companyEmail = "test@gmail.com";
	private String toMail;

	public Mail(String toMail) {
		this.toMail = toMail;
	}

	private Properties getProperties() {
		if (prop == null) {
			prop = new Properties();
			prop.put("mail.smtp.auth", true);
			prop.put("mail.smtp.starttls.enable", "true");
			prop.put("mail.smtp.host", "smtp.mailtrap.io");
			prop.put("mail.smtp.port", "25");
			prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
			// In the properties configuration above, we configured the email host as
			// Mailtrap and use the port provided by the service as well.
			// Now let’s move further by creating a session with our username and password:
		}

		return prop;
	}

	public void sendMail(String subject, String msg) throws AddressException, MessagingException {
		Session session = Session.getInstance(getProperties(), new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		// The username and password are given by the mail service provider alongside
		// the host and port parameters.
		// Now that we have a mail Session object, let’s create a MimeMessage for
		// sending:

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(companyEmail));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail));
		message.setSubject(subject);

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(msg, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		message.setContent(multipart);
		Transport.send(message);
	}
}

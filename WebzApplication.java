
@Service
class NotificationService {
    @Autowired NotificationRepo notifyRepo;
    public void send(UUID userId, String msg) {
        Notification n = new Notification();
        User u = new User(); u.userId = userId;
        n.user = u; n.message = msg;
        notifyRepo.save(n);
    }
}

// ==========================
// 💻 CONTROLLERS
// ==========================
@RestController
@RequestMapping("/api/users")
class UserController {
    @Autowired UserRepo userRepo;
    @Autowired BusinessRepo businessRepo;
    @Autowired FileStorageService fileService;
    @Autowired NotificationService notifyService;

    @PostMapping("/register")
    public User register(@RequestBody User u) { return userRepo.save(u); }

    @PostMapping("/{id}/business")
    public Business addBusiness(@PathVariable UUID id,
        @RequestParam String businessName,
        @RequestParam MultipartFile proofId,
        @RequestParam MultipartFile proofBusiness) throws Exception {

        User user = userRepo.findById(id).orElseThrow();
        Business b = new Business();
        b.user = user;
        b.businessName = businessName;
        b.proofOfIdUrl = fileService.saveFile(proofId);
        b.proofOfBusinessUrl = fileService.saveFile(proofBusiness);
        notifyService.send(user.userId, "✅ Business registered successfully!");
        return businessRepo.save(b);
    }
}

@RestController
@RequestMapping("/api/posts")
class PostController {
    @Autowired PostRepo postRepo;
    @Autowired BoostPaymentRepo boostRepo;
    @Autowired NotificationService notifyService;

    @PostMapping("/create")
    public Post create(@RequestParam UUID userId, @RequestParam String content) {
        Post p = new Post();
        User u = new User(); u.userId = userId;
        p.user = u; p.content = content;
        return postRepo.save(p);
    }

    @PostMapping("/{postId}/boost")
    public BoostPayment boost(@PathVariable UUID postId, @RequestParam double amount) {
        Post post = postRepo.findById(postId).orElseThrow();
        post.isBoosted = true; post.boostStart = new Date();
        Calendar c = Calendar.getInstance(); c.add(Calendar.DATE, 7);
        post.boostEnd = c.getTime(); postRepo.save(post);

        BoostPayment pay = new BoostPayment();
        pay.post = post; pay.user = post.user;
        pay.amountPaid = amount; pay.paymentMethod = "Card";
        boostRepo.save(pay);
        notifyService.send(post.user.userId, "💰 Your post has been boosted for R" + amount);
        return pay;
    }
}

@RestController
@RequestMapping("/api/funding")
class FundingController {
    @Autowired BusinessRepo businessRepo;
    @GetMapping("/check/{businessId}")
    public String check(@PathVariable UUID businessId) {
        Business b = businessRepo.findById(businessId).orElseThrow();
        return b.isExpired ? 
          "❌ Certificate expired. Not eligible for funding." :
          "✅ Your business qualifies for government funding programs.";
    }
}

@RestController
@RequestMapping("/api/complaints")
class ComplaintController {
    @PostMapping("/submit")
    public String submit(@RequestParam String complaintText) {
        return "📩 Complaint submitted: " + complaintText;
    }
}
private class CIPCRegistrationEmailSender {
    public static void sendRegistrationEmail(String userEmail, String subject, String pdfFilePath) throws MessagingException {
        //cipc email address for registration applications
        String toEmail =  "eservicescoreg@cipc.co.za";

    //
    final String fromEmail = "businessEmail";
    final String password = "email password";
    
    //SMTP server config
    properties properties = new Properties ();
    properies.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");
    properties.put("mail.smtp.auth","true");
    properties.put("mail.smtp.starttls.enable","true");
    Aunthenticator auth = new Aunthenticator() {
        protected PasswordAunthentication getPasswordAunthentication(fromEmail , password);

 }   
 };
 Session session = Session.getInstance(properties, auth);

 //construct the email message 
   Message message = new MimeMessage(session);
   message.setFrom(newInternetAddress(fromEmail));
   message.setReceipients(Message.Receipient.TO,InternetAddress.parse(toEmail));
  message.setSubject(subject);

 //Create multipart message for attachment 
 Multipart multipart = new MimeMultipart();

 // email body part
     BodyPart messageBodyPart = newMimeBodyPart();
     messageBodyPart.setText("Dear CIPC, /n/n Please find the attached as the registration application and supporting documents. /n/n  Regards");
multipart.addBodyPart(messageBodyPart);

     //Attachment part
     MimeBodyPart attachmentBodyPart = new MimeBodyPart();
     DataSource source = new FileDataSource (pdfFilePath);
     attachmentBodyPart.setDataHandler(newDataHandler(source));
     attachmentBodyPart.setFileName("CIPC_Registration_Documents.pdf");

     multipart.addBodyPart(attachmentBodyPart);

     message.setContent(multipart);

     //Send email
     Transport.send(message);
     System.out.println("Registration email sent successfully.  BLUESPACE:57 - WebzApplication.java:144");
}
public static void main (String[]args) {
    try {
        //example usage
        sendRegistrationEmail( "user email", "Registration tracking number: 23009", "path to registration_documents.pdf");
    }
    catch(MessagingException e) {
        e.printStackTrace();
    }
    }
}
package com.luxhouse.main.controller.rest;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luxhouse.main.common.APIResponse;
import com.luxhouse.main.domain.Categories;
import com.luxhouse.main.domain.Products;
import com.luxhouse.main.domain.Users;
import com.luxhouse.main.exception.NotFoundEx;
import com.luxhouse.main.exception.NotYetImplementedEx;
import com.luxhouse.main.exception.UserNotFoundException;
import com.luxhouse.main.model.ForgotPasswordDTO;
import com.luxhouse.main.model.PatchDTO;
import com.luxhouse.main.model.ResetPasswordDTO;
import com.luxhouse.main.service.SessionService;
import com.luxhouse.main.service.UserService;
import com.luxhouse.main.util.Utility;

import net.bytebuddy.utility.RandomString;

@RestController
@RequestMapping("/Users")
@CrossOrigin
public class UsersController {

    @Autowired
    UserService usersService;
    
    @Autowired
    SessionService sessionService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JavaMailSender mailSender;

    /**
     * Api get all Users
     * 
     * - GET Method: __/get
     * - Return(JSON): item
     * 
     */
    @GetMapping("/get") // api get all Users
    public List<Users> getAllUsers() {

        return usersService.findAll();
    }
    
    @GetMapping("/get/{start}/{total}") // api get all Products
    public List<Users> getPageUsers(@PathVariable int start, @PathVariable int total) {
        
        List<Users> list = usersService.findAll();
        Collections.reverse(list);
        int toStart = start*total;
        // 12
        // start-total
        // 0-5 : toStart = 0*5, toIndex = toStart+total = 5 
        // 1-5 : toStart = 1*5, toIndex = toStart+total = 10
        // 2-5 : toStart = 2*5, toIndex = toStart+total = 15 => 12
        int toIndex = total + toStart;
        toIndex = toIndex > list.size() ? list.size() : toIndex;
        
        return list.subList(toStart, toIndex);
    }
    
    /**
     * Api get item by id in Products
     * 
     * - GET Method: __/get/{id}
     * - Return(JSON): item
     * 
     */
    @GetMapping("/get/{id}")
    public Users getIdUser(@PathVariable Long id) {
        Optional<Users> idUser = usersService.findById(id);
        if (idUser.isPresent()) {
            return idUser.get();
        }

        return null;
    }
    
    /**
     * Api get item by id in Products
     * 
     * - GET Method: __/get/{id}
     * - Return(JSON): item
     * 
     */
    @GetMapping("/get/users/{id}")
    public Users getEmailUser(@PathVariable Long id) {
        Optional<Users> idUserId = usersService.findById(id);
        if (idUserId.isPresent()) {
            return idUserId.get();
        }

        return null;
    }
    
    @GetMapping("/get/{id}/fullname")
    public String getOneFullName(@PathVariable Long id) {
        Users idUserId = usersService.getOne(id);
        
        return idUserId.getFullname();
    }

    /**
     * Api get last item in Users
     * 
     * - GET Method: __/getLast
     * - Return(JSON): list item
     * 
     */
    @GetMapping("/getLast")
    public Users getLastUsers() {

        List<Users> list = usersService.findAll();
        int totalItem = list.size();

        return list.get(totalItem - 1);
    }

    /**
     * Api get first item in Users
     * 
     * - GET Method: __/getFirst
     * - Return(JSON): item
     * 
     */
    @GetMapping("/getFirst")
    public Users getFirstUsers() {

        return usersService.findAll().get(0);
    }

    /**
     * Api get item by id in Users
     * 
     * - GET Method: __/get/{id}
     * - Return(JSON): item
     * 
     */
//    @GetMapping("/get/{id}")
//    public Users getShopVoucher(@PathVariable Long id) {
//        Optional<Users> itemUsers = usersService.findById(id);
//        if (itemUsers.isPresent()) {
//            return itemUsers.get();
//        }
//
//        return null;
//    }

    /**
     * Api add item to Users
     * 
     * - POST Method: __/add
     * - --data-raw: JSON
     * - Return(JSON): item
     * 
     */
    @PostMapping("/add")
    public Users addUsers(@RequestBody Users Users) {

        usersService.save(Users);

        return getLastUsers();
    }
    
    @PostMapping("/changepassword")
    public APIResponse changePassword(@RequestBody Map<String, String> objMap) {
        
        APIResponse response = new APIResponse();
        
        String password = objMap.get("currencePassword");
        String new_password = objMap.get("newPassword");
        
        Users users = (Users) sessionService.get("loginUser");
        if (users == null) {
            response.setStatus(401);
            response.setData("Vui lòng đăng nhập.");
            response.setError("Please login!");
            return response;
        }
        
        Users user = usersService.findById(users.getId()).get();
        
        System.out.println(new_password);
//        System.out.println("pasword data: " + user.getPassword());
//        System.out.println("Password Nhap tu client: " + password);
//        System.out.println("pass new: " + passwordEncoder.encode(new_password));

        
        if (BCrypt.checkpw(password, user.getPassword()) == true) {
            response.setStatus(200);
            user.setPassword(passwordEncoder.encode(new_password));
            
            usersService.save(user);
            
            response.setData("Đổi mật khẩu thành công!");
        }else {
            response.setStatus(104);
            response.setData("Mật khẩu không đúng!");
        }

        return response;
    }
    
    @PostMapping("/forgot-password")
    public APIResponse forgotPassword(@RequestBody ForgotPasswordDTO emailDTO, HttpServletRequest request) {
        
        APIResponse response = new APIResponse();
        
        String email = emailDTO.getEmail();
        String token = RandomString.make(50);

        
        try {
        	response.setStatus(200);
        	response.setData("Success");
        	usersService.updateResetPassword(token, email);
        	
        	String resetPasswordLink = Utility.getSiteURL(request) + "/users/reset-password?token=" + token;
        	
        	System.out.println(resetPasswordLink);
        	
        	sendEmail(email, resetPasswordLink);
     	
		} catch (UserNotFoundException e) {
			// TODO: handle exception
			response.setStatus(401);
			response.setData("Email không tồn tại!");
		} catch (UnsupportedEncodingException | MessagingException e) {
			response.setStatus(401);
			response.setData("Email không tồn tại!");
		}

        return response;
    }
    
    @PostMapping("/reset-password")
    public APIResponse resetPassword(@RequestBody ResetPasswordDTO pass, @Param(value = "token") String token) {
        
        APIResponse response = new APIResponse();
        
        String newPass = pass.getNewPass();
        
        Users users = usersService.getToken(token);
        if (users == null) {
        	response.setStatus(401);
			response.setData("Invalid Token!");
			
		}else {
			usersService.updatePassword(users, newPass);
			response.setStatus(200);
			response.setData("You have successfully chaged your password!");
			sessionService.remove("loginUser");
		}
          
        return response;
    }

    private void sendEmail(String email, String resetPasswordLink) throws UnsupportedEncodingException, MessagingException {
		// TODO Auto-generated method stub
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom("contact@gmail.com", "Support");
		helper.setTo(email);
		
		String subject = "Here's the link to reset your password";
		
		String content = "<body marginheight=\"0\" topmargin=\"0\" marginwidth=\"0\" style=\"margin: 0px; background-color: #f2f3f8;\" leftmargin=\"0\">\r\n"
				+ "    <!--100% body table-->\r\n"
				+ "    <table cellspacing=\"0\" border=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#f2f3f8\"\r\n"
				+ "        style=\"@import url(https://fonts.googleapis.com/css?family=Rubik:300,400,500,700|Open+Sans:300,400,600,700); font-family: 'Open Sans', sans-serif;\">\r\n"
				+ "        <tr>\r\n"
				+ "            <td>\r\n"
				+ "                <table style=\"background-color: #f2f3f8; max-width:670px;  margin:0 auto;\" width=\"100%\" border=\"0\"\r\n"
				+ "                    align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td style=\"height:80px;\">&nbsp;</td>\r\n"
				+ "                    </tr>\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td style=\"text-align:center;\">\r\n"
				+ "                          <a href=\"https://rakeshmandal.com\" title=\"logo\" target=\"_blank\">\r\n"
				+ "                            <img width=\"60\" src=\"https://i.ibb.co/hL4XZp2/android-chrome-192x192.png\" title=\"logo\" alt=\"logo\">\r\n"
				+ "                          </a>\r\n"
				+ "                        </td>\r\n"
				+ "                    </tr>\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td style=\"height:20px;\">&nbsp;</td>\r\n"
				+ "                    </tr>\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td>\r\n"
				+ "                            <table width=\"95%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\r\n"
				+ "                                style=\"max-width:670px;background:#fff; border-radius:3px; text-align:center;-webkit-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);-moz-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);box-shadow:0 6px 18px 0 rgba(0,0,0,.06);\">\r\n"
				+ "                                <tr>\r\n"
				+ "                                    <td style=\"height:40px;\">&nbsp;</td>\r\n"
				+ "                                </tr>\r\n"
				+ "                                <tr>\r\n"
				+ "                                    <td style=\"padding:0 35px;\">\r\n"
				+ "                                        <h1 style=\"color:#1e1e2d; font-weight:500; margin:0;font-size:32px;font-family:'Rubik',sans-serif;\">You have\r\n"
				+ "                                            requested to reset your password</h1>\r\n"
				+ "                                        <span\r\n"
				+ "                                            style=\"display:inline-block; vertical-align:middle; margin:29px 0 26px; border-bottom:1px solid #cecece; width:100px;\"></span>\r\n"
				+ "                                        <p style=\"color:#455056; font-size:15px;line-height:24px; margin:0;\">\r\n"
				+ "                                            We cannot simply send you your old password. A unique link to reset your\r\n"
				+ "                                            password has been generated for you. To reset your password, click the\r\n"
				+ "                                            following link and follow the instructions.\r\n"
				+ "                                        </p>\r\n"
				+ "                                        <a href=\"" + resetPasswordLink + "\"\r\n"
				+ "                                            style=\"background:#20e277;text-decoration:none !important; font-weight:500; margin-top:35px; color:#fff;text-transform:uppercase; font-size:14px;padding:10px 24px;display:inline-block;border-radius:50px;\">Reset\r\n"
				+ "                                            Password</a>\r\n"
				+ "                                    </td>\r\n"
				+ "                                </tr>\r\n"
				+ "                                <tr>\r\n"
				+ "                                    <td style=\"height:40px;\">&nbsp;</td>\r\n"
				+ "                                </tr>\r\n"
				+ "                            </table>\r\n"
				+ "                        </td>\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td style=\"height:20px;\">&nbsp;</td>\r\n"
				+ "                    </tr>\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td style=\"text-align:center;\">\r\n"
				+ "                            <p style=\"font-size:14px; color:rgba(69, 80, 86, 0.7411764705882353); line-height:18px; margin:0 0 0;\">&copy; <strong>www.rakeshmandal.com</strong></p>\r\n"
				+ "                        </td>\r\n"
				+ "                    </tr>\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td style=\"height:80px;\">&nbsp;</td>\r\n"
				+ "                    </tr>\r\n"
				+ "                </table>\r\n"
				+ "            </td>\r\n"
				+ "        </tr>\r\n"
				+ "    </table>\r\n"
				+ "    <!--/100% body table-->\r\n"
				+ "</body>";
		
//		String content = "<p>Hello,</p>"
//				+"<p>You have requested to reset your password</p>"
//				+"<p>Link reset password</p>"
//				+"<a href=\""+ resetPasswordLink + "\" >Change your password</a>";
		
		helper.setSubject(subject);
		helper.setText(content, true);
		
		mailSender.send(message);
	}

	/**
     * Api update item
     * 
     * - PUT Method: __/update
     * - --data-raw: JSON
     * - Return(JSON): item
     * 
     */
    @PutMapping("/update")
    public Users updateUsers(@RequestBody Users Users) {
        
        Users user = (Users) sessionService.get("loginUser");
        if (user == null) return user;
        String password = usersService.findById(user.getId()).get().getPassword();
        
        Users.setPassword(password);
        Users.setUsername(user.getUsername());
        Users.setEmail(user.getEmail());
        Users.setPhone(user.getPhone());
        Users.setRoles(user.getRoles());
        
        boolean status = true;
        
        Users.setStatus(status);

        usersService.save(Users);

        return usersService.findById(Users.getId()).get();
    }
    
    
    @PatchMapping("/update/{id}")
    public ResponseEntity<Boolean> updatePartially(@PathVariable(name = "id") Long id,
        @RequestBody PatchDTO dto) throws NotYetImplementedEx, NotFoundEx {
      // skipping validations for brevity
      if (dto.getOp().equalsIgnoreCase("update")) {
        boolean result = usersService.partialUpdate(id, dto.getKey(), dto.getValue());
        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
      } else {
        throw new NotYetImplementedEx("NOT_YET_IMPLEMENTED");
      }
    }


    /**
     * Api delete item
     * 
     * - DELETE Method: __/delete/{id}
     * - Return(JSON): item
     * 
     */
//    @DeleteMapping("/delete/{id}")
//    public Users deleteUsers(@PathVariable Long id) {
//
//        Users item = usersService.findById(id).get();
//
//        usersService.delete(item);
//
//        return item;
//    }
}

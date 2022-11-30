package kz.narxoz.Isc2.controllers;

import kz.narxoz.Isc2.models.Auth.Users;
import kz.narxoz.Isc2.models.Bouquets;
import kz.narxoz.Isc2.repository.BouquetsRepository;
import kz.narxoz.Isc2.services.impl.UserServices;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class MainController {

  @Autowired
  private BouquetsRepository bouquetsRepository;

  @Autowired
  private UserServices userServices;


  @Value("${file.bouquets.viewPath}")
  private String viewPath;

  @Value("${file.bouquets.uploadPath}")
  private String uploadPath;

  @Value("${file.bouquets.defaultPicture}")
  private String defaultPicture;


  @GetMapping(value = "/")
  public String indexPage(Model model) {
    List<Bouquets> bouquets = bouquetsRepository.findAll();
    model.addAttribute("currentUser" ,getCurrentUser());
    model.addAttribute("bouquets", bouquets);
    return "index";
  }

  @GetMapping(value = "/addBouquet")
  public String addPage(Model model) {
    model.addAttribute("currentUser" ,getCurrentUser());
    return "addBouquet";
  }

  @GetMapping(value = "/login")
  public String login(Model model) {
    model.addAttribute("currentUser" ,getCurrentUser());
    return "login";
  }

  @GetMapping(value = "/profile")
  @PreAuthorize("isAuthenticated()")
  public String profile(Model model) {
    model.addAttribute("currentUser" ,getCurrentUser());
    return "profile";
  }


  @PostMapping(value = "/addNewBouquet")
  public String addNewBouquet(@RequestParam(name = "name") String name,
                              @RequestParam(name = "composition") String composition,
                              @RequestParam(name = "description") String description,
                              @RequestParam(name = "price") int price,
                              @RequestParam(name = "size") String size,
                              @RequestParam(name = "b_image") MultipartFile file,
                              @RequestParam(name = "b_image2") MultipartFile file2,
                              @RequestParam(name = "b_image3") MultipartFile file3) {


    if ((file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))
        && (file2.getContentType().equals("image/jpeg") || file2.getContentType().equals("image/png"))
        && (file3.getContentType().equals("image/jpeg") || file3.getContentType().equals("image/png"))
    ) {
      try {
        String picName = DigestUtils.sha1Hex("picture" + composition);
        String picName2 = DigestUtils.sha1Hex("picture" + description);
        String picName3 = DigestUtils.sha1Hex("picture" + price);

        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadPath + picName + ".jpg");
        Files.write(path, bytes);

        byte[] bytes2 = file2.getBytes();
        Path path2 = Paths.get(uploadPath + picName2 + ".jpg");
        Files.write(path2, bytes2);

        byte[] bytes3 = file3.getBytes();
        Path path3 = Paths.get(uploadPath + picName3 + ".jpg");
        Files.write(path3, bytes3);


        Bouquets bouquets = new Bouquets();
        bouquets.setName(name);
        bouquets.setComposition(composition);
        bouquets.setDescription(description);
        bouquets.setPrice(price);
        bouquets.setSize(size);
        bouquets.setB_image(picName);
        bouquets.setB_image2(picName2);
        bouquets.setB_image3(picName3);

        bouquetsRepository.save(bouquets);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return "redirect:/";

  }

  @GetMapping(value = "/viewphoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
  public @ResponseBody
  byte[] viewProfilePhoto(@PathVariable(name = "url") String url) throws Exception {

    String pictureUrl = viewPath + defaultPicture;

    if (url != null) {
      pictureUrl = viewPath + url + ".jpg";
    }

    InputStream in;

    try {

      ClassPathResource resource = new ClassPathResource(pictureUrl);
      in = resource.getInputStream();

    } catch (Exception e) {

      ClassPathResource resource = new ClassPathResource(viewPath + defaultPicture);
      in = resource.getInputStream();
      e.printStackTrace();
    }

    return IOUtils.toByteArray(in);

  }


  @GetMapping(value = "/detailsBouquet{id}")
  public String details(@PathVariable(name = "id") Long id, Model model) {
    Bouquets bouquets = bouquetsRepository.findById(id).orElse(null);
    model.addAttribute("currentUser" ,getCurrentUser());
    model.addAttribute("bouquets", bouquets);
    return "detailsBouquets";
  }


  @PostMapping(value = "/saveBouquet")
  public String saveBouquet(@RequestParam(name = "id") Long id,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "composition") String composition,
                            @RequestParam(name = "description") String description,
                            @RequestParam(name = "price") int price,
                            @RequestParam(name = "size") String size,
                            @RequestParam(name = "b_image") MultipartFile file,
                            @RequestParam(name = "b_image2") MultipartFile file2,
                            @RequestParam(name = "b_image3") MultipartFile file3) {


      try {
        String picName = DigestUtils.sha1Hex("picture" + composition);
        String picName2 = DigestUtils.sha1Hex("picture" + description);
        String picName3 = DigestUtils.sha1Hex("picture" + price);

        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadPath + picName + ".jpg");
        Files.write(path, bytes);

        byte[] bytes2 = file2.getBytes();
        Path path2 = Paths.get(uploadPath + picName2 + ".jpg");
        Files.write(path2, bytes2);

        byte[] bytes3 = file3.getBytes();
        Path path3 = Paths.get(uploadPath + picName3 + ".jpg");
        Files.write(path3, bytes3);


        Bouquets bouquets = bouquetsRepository.findById(id).orElse(null);

        if (bouquets != null) {
          bouquets.setName(name);
          bouquets.setComposition(composition);
          bouquets.setDescription(description);
          bouquets.setPrice(price);
          bouquets.setSize(size);
          bouquets.setB_image(picName);
          bouquets.setB_image2(picName2);
          bouquets.setB_image3(picName3);

          bouquetsRepository.save(bouquets);
          return "redirect:/";
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    return "redirect:/";
  }



  @PostMapping(value = "/deleteBouquets")
  public String deleteBouquets(
      @RequestParam(name = "id") Long id  ) {

    Bouquets bouquets  = bouquetsRepository.findById(id).orElse(null);

    if(bouquets!=null){
      bouquetsRepository.delete(bouquets);
      return "redirect:/";
    }
    return "redirect:/";
  }

  @GetMapping(value = "/register")
  public String register(Model model){
    model.addAttribute("currentUser",getCurrentUser());
    return "register";
  }

  @PostMapping(value = "/register")
  public String toRegister(@RequestParam(name = "user_email") String email,
                           @RequestParam(name = "user_password") String password,
                           @RequestParam(name = "user_rePassword") String rePassword,
                           @RequestParam(name = "user_fullName") String fullName){

    if (password.equals(rePassword)){
      Users newUser = new Users();
      newUser.setPassword(password);
      newUser.setFull_name(fullName);
      newUser.setEmail(email);

      if (userServices.createUser(newUser)!=null){
        return "redirect:/profile";
      }
    }

    return "redirect:/register?error";
  }


  private Users getCurrentUser(){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if(!(authentication instanceof AnonymousAuthenticationToken)){
      Users currentUser = (Users) authentication.getPrincipal();
      return currentUser;
    }
    return null;
  }

}

package kz.narxoz.Isc2.controllers;

import kz.narxoz.Isc2.models.Auth.Roles;
import kz.narxoz.Isc2.models.Auth.Users;
import kz.narxoz.Isc2.models.Bouquets;
import kz.narxoz.Isc2.models.EdibleBouquet;
import kz.narxoz.Isc2.models.FlowerInBox;
import kz.narxoz.Isc2.models.Status;
import kz.narxoz.Isc2.repository.*;
import kz.narxoz.Isc2.services.impl.UserServices;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.Role;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Controller
public class MainController {


  @Autowired
  private BouquetsRepository bouquetsRepository;

  @Autowired
  private UserServices userServices;

  @Autowired
  private Flower_boxRepository flower_boxRepository;

  @Autowired
  private Edible_bouquetRepository edible_bouquetRepository;

  @Autowired
  private StatusRepository statusRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Value("${file.bouquets.viewPath}")
  private String viewPath;

  @Value("${file.bouquets.uploadPath}")
  private String uploadPath;

  @Value("${file.bouquets.defaultPicture}")
  private String defaultPicture;


  @GetMapping(value = "/")
  public String indexPage(Model model) {
    List<Bouquets> bouquets = bouquetsRepository.findAll();
    List<FlowerInBox> flowerInBoxes = flower_boxRepository.findAll();
    model.addAttribute("bouquets", bouquets);
    List<Status> status = statusRepository.findAll();
    List<EdibleBouquet> edibleBouquets = edible_bouquetRepository.findAll();
    model.addAttribute("currentUser", getCurrentUser());
    model.addAttribute("status", status);
    model.addAttribute("edibleBouquets", edibleBouquets);
    model.addAttribute("flowerInBoxes", flowerInBoxes);

    return "index";
  }

  @RequestMapping(value = "/filter")
  public String filterPage(Model model, @Param("priceCategory") Integer priceCategory,
                           @Param("priceCategory2") Integer priceCategory2) {
    List<Bouquets> bouquets = bouquetsRepository.findAll(priceCategory, priceCategory2);
    model.addAttribute("bouquets", bouquets);
    model.addAttribute("priceCategory", priceCategory);
    model.addAttribute("priceCategory2", priceCategory);
    List<Status> status = statusRepository.findAll();
    model.addAttribute("currentUser", getCurrentUser());
    model.addAttribute("status", status);
    return "filterPage";
  }



  @RequestMapping(value = "/catalogue")
  public String catalogue(Model model ,@Param("priceCategory") Integer priceCategory,
                          @Param("priceCategory2") Integer priceCategory2) {
    List<Bouquets> bouquets = bouquetsRepository.findAll(priceCategory, priceCategory2);
    List<FlowerInBox> flowerInBoxes = flower_boxRepository.findAll(priceCategory, priceCategory2);
    model.addAttribute("bouquets", bouquets);
    List<Status> status = statusRepository.findAll();
    List<EdibleBouquet> edibleBouquets = edible_bouquetRepository.findAll(priceCategory,priceCategory2);
    model.addAttribute("currentUser", getCurrentUser());
    model.addAttribute("status", status);
    model.addAttribute("edibleBouquets", edibleBouquets);
    model.addAttribute("flowerInBoxes", flowerInBoxes);
    return "catalogue";
  }


  @RequestMapping(value = "/search")
  public String search(Model model, @Param("keyword") String keyword) {
    List<Bouquets> bouquets = bouquetsRepository.findAll(keyword);
    List<EdibleBouquet> edibleBouquets = edible_bouquetRepository.findAll(keyword);
    List<FlowerInBox> flowerInBoxes = flower_boxRepository.findAll(keyword);
    List<Status> status = statusRepository.findAll();
    model.addAttribute("currentUser", getCurrentUser());
    model.addAttribute("bouquets", bouquets);
    model.addAttribute("edibleBouquets", edibleBouquets);
    model.addAttribute("flowerInBoxes", flowerInBoxes);
    model.addAttribute("status", status);
    model.addAttribute("keyword", keyword);
    return "search";
  }


  @GetMapping(value = "/settings")
  @PreAuthorize("hasAnyRole('ROLE_MODERATOR')")
  public String addPage(Model model) {
    List<EdibleBouquet> edibleBouquets = edible_bouquetRepository.findAll();
    List<Bouquets> bouquets = bouquetsRepository.findAll();
    List<FlowerInBox> flowerInBoxes = flower_boxRepository.findAll();
    model.addAttribute("currentUser", getCurrentUser());
    model.addAttribute("bouquets", bouquets);
    model.addAttribute("flowerInBoxes", flowerInBoxes);
    model.addAttribute("edibleBouquets", edibleBouquets);
    List<Status> status = statusRepository.findAll();
    model.addAttribute("status", status);
    List<Users> users = userRepository.findAll();
    model.addAttribute("users", users);
    List<Roles> roles = roleRepository.findAll();
    model.addAttribute("roles", roles);
    return "settings";
  }

  @GetMapping(value = "/login")
  public String login(Model model) {
    model.addAttribute("currentUser", getCurrentUser());
    return "login";
  }


  @GetMapping(value = "/profile")
  @PreAuthorize("isAuthenticated()")
  public String profile(Model model) {
    model.addAttribute("currentUser", getCurrentUser());
    return "profile";
  }


  @PostMapping(value = "/addNewBouquet")
  public String addNewBouquet(@RequestParam(name = "name") String name,
                              @RequestParam(name = "composition") String composition,
                              @RequestParam(name = "description") String description,
                              @RequestParam(name = "price") int price,
                              @RequestParam(name = "status_id") Long status_id,
                              @RequestParam(name = "b_image") MultipartFile file) {


    if ((file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))

    ) {
      try {
        String picName = DigestUtils.sha1Hex("picture" + composition);
        String picName2 = DigestUtils.sha1Hex("picture" + description);
        String picName3 = DigestUtils.sha1Hex("picture" + price);

        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadPath + picName + ".jpg");
        Files.write(path, bytes);

        Status status = statusRepository.findById(status_id).orElse(null);

        if (status != null) {
          Bouquets bouquets = new Bouquets();
          bouquets.setName(name);
          bouquets.setComposition(composition);
          bouquets.setDescription(description);
          bouquets.setStatus(status);
          bouquets.setPrice(price);
          bouquets.setB_image(picName);

          bouquetsRepository.save(bouquets);
        }


      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return "redirect:/settings";

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
    List<EdibleBouquet> edibleBouquets = edible_bouquetRepository.findAll();
    model.addAttribute("edibleBouquets", edibleBouquets);
    model.addAttribute("currentUser", getCurrentUser());
    model.addAttribute("bouquets", bouquets);
    List<Status> status = statusRepository.findAll();
    model.addAttribute("status", status);
    return "detailsBouquets";
  }


  @PostMapping(value = "/saveBouquet")
  public String saveBouquet(@RequestParam(name = "id") Long id,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "composition") String composition,
                            @RequestParam(name = "description") String description,
                            @RequestParam(name = "price") int price,
                            @RequestParam(name = "status_id") Long status_id,
                            @RequestParam(name = "b_image") MultipartFile file) {


    try {
      String picName = DigestUtils.sha1Hex("picture" + composition);

      byte[] bytes = file.getBytes();
      Path path = Paths.get(uploadPath + picName + ".jpg");
      Files.write(path, bytes);


      Bouquets bouquets = bouquetsRepository.findById(id).orElse(null);
      Status status = statusRepository.findById(status_id).orElse(null);

      if (bouquets != null) {
        bouquets.setName(name);
        bouquets.setComposition(composition);
        bouquets.setDescription(description);
        bouquets.setPrice(price);
        bouquets.setB_image(picName);
        bouquets.setStatus(status);

        bouquetsRepository.save(bouquets);
        return "redirect:/detailsBouquet" + id;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/";
  }

  @PostMapping(value = "/deleteAccount")
  public String deleteAccount(
      @RequestParam(name = "id") Long id,
      @RequestParam(name = "email") String email) {

    Users user = userRepository.findById(id).orElse(null);

    if (Objects.equals(email, "admin@gmail.com")) {
      return "redirect:/settings";
    }

    if (user != null) {
        userRepository.delete(user);
        return "redirect:/settings";
    }
    return "redirect:/settings";
  }

  @PostMapping(value = "/deleteBouquets")
  public String deleteBouquets(
      @RequestParam(name = "id") Long id) {

    Bouquets bouquets = bouquetsRepository.findById(id).orElse(null);

    if (bouquets != null) {
      bouquetsRepository.delete(bouquets);
      return "redirect:/";
    }
    return "redirect:/";
  }

  @GetMapping(value = "/register")
  public String register(Model model) {
    model.addAttribute("currentUser", getCurrentUser());
    return "register";
  }

  @PostMapping(value = "/register")
  public String toRegister(@RequestParam(name = "user_email") String email,
                           @RequestParam(name = "user_password") String password,
                           @RequestParam(name = "user_rePassword") String rePassword,
                           @RequestParam(name = "user_fullName") String fullName) {

    if (password.equals(rePassword)) {
      Users newUser = new Users();
      newUser.setPassword(password);
      newUser.setFull_name(fullName);
      newUser.setEmail(email);

      if (userServices.createUser(newUser) != null) {
        return "redirect:/profile";
      }
    }

    return "redirect:/register?error";
  }


  private Users getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!(authentication instanceof AnonymousAuthenticationToken)) {
      Users currentUser = (Users) authentication.getPrincipal();
      return currentUser;
    }
    return null;
  }

}

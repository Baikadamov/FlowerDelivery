package kz.narxoz.Isc2.controllers;

import kz.narxoz.Isc2.models.Auth.Users;
import kz.narxoz.Isc2.models.Bouquets;
import kz.narxoz.Isc2.models.EdibleBouquet;
import kz.narxoz.Isc2.models.FlowerInBox;
import kz.narxoz.Isc2.repository.BouquetsRepository;
import kz.narxoz.Isc2.repository.Edible_bouquetRepository;
import kz.narxoz.Isc2.repository.Flower_boxRepository;
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
public class FlowersController {

  @Autowired
  private Flower_boxRepository flower_boxRepository;

  @Autowired
  private Edible_bouquetRepository edible_bouquetRepository;

  @Autowired
  private BouquetsRepository bouquetsRepository;

  @Value("${file.bouquets.viewPath}")
  private String viewPath;

  @Value("${file.bouquets.uploadPath}")
  private String uploadPath;

  @Value("${file.bouquets.defaultPicture}")
  private String defaultPicture;



  @GetMapping(value = "/flowerInbox")
  public String flowerInbox(Model model) {
    List<FlowerInBox> flowerInBoxes = flower_boxRepository.findAll();
    model.addAttribute("currentUser" ,getCurrentUser());
    model.addAttribute("flowerInBoxes", flowerInBoxes);
    return "flowerInbox";
  }

  @GetMapping(value = "/edibleBouquet")
  public String edibleBouquet(Model model) {
    List<EdibleBouquet> edibleBouquets = edible_bouquetRepository.findAll();
    model.addAttribute("currentUser" ,getCurrentUser());
    model.addAttribute("edibleBouquets", edibleBouquets);

    return "edibleBouquet";
  }

  @GetMapping(value = "/detailsFlowerInBox{id}")
  public String details(@PathVariable(name = "id") Long id, Model model) {
    FlowerInBox flowerInBox = flower_boxRepository.findById(id).orElse(null);
    List<Bouquets> bouquets = bouquetsRepository.findAll();
    model.addAttribute("currentUser" ,getCurrentUser());
    model.addAttribute("flowerInBox", flowerInBox);
    model.addAttribute("bouquets",bouquets);
    return "detailsFlowerInBox";
  }

  @GetMapping(value = "/detailsEdibleBouquet{id}")
  public String detailsEdibleBouquet(@PathVariable(name = "id") Long id, Model model) {
    EdibleBouquet edibleBouquet = edible_bouquetRepository.findById(id).orElse(null);
    model.addAttribute("currentUser" ,getCurrentUser());
    model.addAttribute("edibleBouquet", edibleBouquet);
    List<Bouquets> bouquets = bouquetsRepository.findAll();
    model.addAttribute("bouquets",bouquets);
    return "detailsEdibleBouquet";
  }

  @PostMapping(value = "/addNewFlowerInBox")
  public String addNewFlowerInBox(@RequestParam(name = "name") String name,
                              @RequestParam(name = "composition") String composition,
                              @RequestParam(name = "description") String description,
                              @RequestParam(name = "price") int price,
                              @RequestParam(name = "image") MultipartFile file ) {


    if ( (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))
    ) {
      try {
        String picName = DigestUtils.sha1Hex("picture" + description);

        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadPath + picName + ".jpg");
        Files.write(path, bytes);


        FlowerInBox flowerInBox = new FlowerInBox();
        flowerInBox.setName(name);
        flowerInBox.setComposition(composition);
        flowerInBox.setDescription(description);
        flowerInBox.setPrice(price);
        flowerInBox.setImage(picName);

        flower_boxRepository.save(flowerInBox);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return "redirect:/settings";

  }


  @PostMapping(value = "/addNewEdibleBouquet")
  public String addNewEdibleBouquet(@RequestParam(name = "name") String name,
                                  @RequestParam(name = "composition") String composition,
                                  @RequestParam(name = "description") String description,
                                  @RequestParam(name = "price") int price,
                                  @RequestParam(name = "image") MultipartFile file ) {


    if ( (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))
    ) {
      try {
        String picName = DigestUtils.sha1Hex("picture" + description);

        byte[] bytes = file.getBytes();
        Path path = Paths.get(uploadPath + picName + ".jpg");
        Files.write(path, bytes);


        EdibleBouquet edibleBouquet = new EdibleBouquet();
        edibleBouquet.setName(name);
        edibleBouquet.setComposition(composition);
        edibleBouquet.setDescription(description);
        edibleBouquet.setPrice(price);
        edibleBouquet.setImage(picName);

        edible_bouquetRepository.save(edibleBouquet);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return "redirect:/settings";

  }



  @PostMapping(value = "/saveFlowerInBox")
  public String saveFlowerInBox(@RequestParam(name = "id") Long id,
                            @RequestParam(name = "name") String name,
                            @RequestParam(name = "composition") String composition,
                            @RequestParam(name = "description") String description,
                            @RequestParam(name = "price") int price,
                            @RequestParam(name = "image") MultipartFile file ) {


    try {
      String picName = DigestUtils.sha1Hex("picture" + composition);

      byte[] bytes = file.getBytes();
      Path path = Paths.get(uploadPath + picName + ".jpg");
      Files.write(path, bytes);



      FlowerInBox flowerInBox = flower_boxRepository.findById(id).orElse(null);

      if (flowerInBox != null) {
        flowerInBox.setName(name);
        flowerInBox.setComposition(composition);
        flowerInBox.setDescription(description);
        flowerInBox.setPrice(price);
        flowerInBox.setImage(picName);

        flower_boxRepository.save(flowerInBox);
        return "redirect:/flowerInbox";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/flowerInbox";
  }


  @PostMapping(value = "/saveEdibleBouquet")
  public String saveEdibleBouquet(@RequestParam(name = "id") Long id,
                                @RequestParam(name = "name") String name,
                                @RequestParam(name = "composition") String composition,
                                @RequestParam(name = "description") String description,
                                @RequestParam(name = "price") int price,
                                @RequestParam(name = "image") MultipartFile file ) {


    try {
      String picName = DigestUtils.sha1Hex("picture" + name);

      byte[] bytes = file.getBytes();
      Path path = Paths.get(uploadPath + picName + ".jpg");
      Files.write(path, bytes);



      EdibleBouquet edibleBouquet = edible_bouquetRepository.findById(id).orElse(null);

      if (edibleBouquet != null) {
        edibleBouquet.setName(name);
        edibleBouquet.setComposition(composition);
        edibleBouquet.setDescription(description);
        edibleBouquet.setPrice(price);
        edibleBouquet.setImage(picName);

        edible_bouquetRepository.save(edibleBouquet);
        return "redirect:/edibleBouquet";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/edibleBouquet";
  }



  @PostMapping(value = "/deleteFlowerInBox")
  public String deleteBouquets(
      @RequestParam(name = "id") Long id  ) {

    FlowerInBox flowerInBox  = flower_boxRepository.findById(id).orElse(null);

    if(flowerInBox!=null){
      flower_boxRepository.delete(flowerInBox);
      return "redirect:/flowerInbox";
    }
    return "redirect:/flowerInbox";
  }

  @PostMapping(value = "/deleteEdibleBouquet")
  public String deleteEdibleBouquet(
      @RequestParam(name = "id") Long id  ) {

    EdibleBouquet edibleBouquet  = edible_bouquetRepository.findById(id).orElse(null);

    if(edibleBouquet!=null){
      edible_bouquetRepository.delete(edibleBouquet);
      return "redirect:/edibleBouquet";
    }
    return "redirect:/edibleBouquet";
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

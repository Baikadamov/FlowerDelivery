package kz.narxoz.Isc2.controllers;

import kz.narxoz.Isc2.models.Bouquets;
import kz.narxoz.Isc2.repository.BouquetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private BouquetsRepository bouquetsRepository ;

    @GetMapping(value = "/")
    public String indexPage(Model model) {
        List<Bouquets> bouquets = bouquetsRepository.findAll();
        model.addAttribute("employees", bouquets);
        return "index";
    }



}

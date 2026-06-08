package com.example.flippedclass.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mentor/spaces/{spaceId}/members")
public class LearningSpaceMemberUIController {

    @GetMapping
    public String membersPage(@PathVariable Long spaceId, Model model) {
        model.addAttribute("spaceId", spaceId);
        return "mentor/space-members";
    }
}

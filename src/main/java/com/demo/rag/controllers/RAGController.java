package com.demo.rag.controllers;

import com.demo.rag.service.RAGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RAGController {

    @Autowired
    private RAGService ragService;

    @GetMapping("/rag")
    public String index(@RequestParam(name = "question", defaultValue = "") String question, Model model) {
        if (question.isEmpty()) return "rag";
        model.addAttribute("question", question);
        model.addAttribute("response", ragService.askLLM(question));
        return "rag";
    }
}

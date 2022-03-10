package com.decagon.fitnessoapp.controller;

import com.decagon.fitnessoapp.dto.ContactResponse;
import com.decagon.fitnessoapp.dto.transactionDto.ContactRequest;
import com.decagon.fitnessoapp.model.user.Contact;
import com.decagon.fitnessoapp.service.ContactService;
import com.decagon.fitnessoapp.service.serviceImplementation.ContactServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ContactController {

    private ContactService contactService;

    @PostMapping("/contact/save")
    public ResponseEntity<ContactRequest> saveContact(@Valid @RequestBody ContactRequest contactRequest){
        return ResponseEntity.ok(contactService.save(contactRequest));
    }

    @GetMapping("/getContacts")
    public ResponseEntity<List<Contact>> getAllContacts(){
        return ResponseEntity.ok(contactService.getAllContact());
    }
}

package com.mndro.calista.controller.auth;


import com.mndro.calista.service.impl.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping("/send-active-cycle")
    public ResponseEntity<String> sendReminders() {
        int count = reminderService.sendReminderToUsersWithActiveCycles();
        return ResponseEntity.ok("📧 Sent reminders to " + count + " users with active cycles.");
    }
}

package com.truvish.truvishbackend.redemption;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/redemption-history")
public class RedemptionHistoryController {

    private final UserRedemptionService userRedemptionService;

    public RedemptionHistoryController(UserRedemptionService userRedemptionService) {
        this.userRedemptionService = userRedemptionService;
    }

    @GetMapping("/client/{clientId}")
    public List<RedemptionHistoryItemResponse> getClientHistory(@PathVariable Long clientId) {
        // This service merges digital redemption rows and physical TruBlankCode
        // assignment/lifecycle rows, including codes that have not been redeemed.
        return userRedemptionService.getHistoryByClientId(clientId);
    }
}
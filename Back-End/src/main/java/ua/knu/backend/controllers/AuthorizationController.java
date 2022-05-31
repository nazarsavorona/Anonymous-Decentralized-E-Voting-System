package ua.knu.backend.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ua.knu.backend.hashalgorithms.SHA256;
import ua.knu.backend.identityprovider.IdentityProvider;
import ua.knu.backend.services.RingSignatureService;
import ua.knu.backend.services.VoterService;
import ua.knu.backend.user.Voter;

import java.math.BigInteger;

@RestController
@AllArgsConstructor
@Slf4j
public class AuthorizationController {
    private final VoterService voterService;

    @PostMapping(value = "/voter/{privateKey}", produces = "application/json")
    @ResponseBody
    public BigInteger authorizeVoter(@PathVariable("privateKey") BigInteger privateKey){
        if (this.voterService.setVoterPrivateKey(privateKey, new Voter(new RingSignatureService(new SHA256()), new IdentityProvider()))){
            return privateKey;
        }

        return BigInteger.valueOf(-1);
    }
}

package ua.knu.backend.services;

import org.springframework.stereotype.Service;
import ua.knu.backend.user.Voter;

import java.math.BigInteger;

@Service
public class VoterService {
    public boolean setVoterPrivateKey(BigInteger privateKey, Voter voter){
        return voter.setPrivateKey(privateKey);
    }


}

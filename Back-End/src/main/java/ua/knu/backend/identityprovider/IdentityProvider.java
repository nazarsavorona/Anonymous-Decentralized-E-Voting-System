package ua.knu.backend.identityprovider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Component;
import ua.knu.backend.dto.ECPointDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@Getter
public class IdentityProvider {
    List<ECPoint> publicKeys;
    List<Integer> candidateIDs;

    public IdentityProvider() {
        ECPoint pKey1 = new ECPointDTO(new BigInteger("39094306632152602176226349534631573863161895099536412225981229241163899705092"),
                new BigInteger("3785904575628717555583634968245295067715295405127590019965664207786558200844")).convertToECPoint();
        ECPoint pKey2 = new ECPointDTO(new BigInteger("17572968293023113285096543749135516076772974117434044800182182281469438135043"),
                new BigInteger("41316801335387697921382734130408539216296707318606713495545956465533665505601")).convertToECPoint();
        ECPoint pKey3 = new ECPointDTO(new BigInteger("8637272125031190398057964043264678875089873431911689275944414664512679023633"),
                new BigInteger("56593434185660555203880791406370779893012801865215108653167524006760560906559")).convertToECPoint();
        ECPoint pKey4 = new ECPointDTO(new BigInteger("56054482177314319085280802439840680936950952518291665980876670617673102310364"),
                new BigInteger("2031332765442012735796368105015307709962744137100991597075474729183777559059")).convertToECPoint();
        ECPoint pKey5 = new ECPointDTO(new BigInteger("25232152776750399634646111436622545398197244945385242132545616280987159677768"),
                new BigInteger("44021090771738544595885530353046461271513178890021168344206904165385758354622")).convertToECPoint();

        publicKeys = new ArrayList<>();

        publicKeys.add(pKey1);
        publicKeys.add(pKey2);
        publicKeys.add(pKey3);
        publicKeys.add(pKey4);
        publicKeys.add(pKey5);

        candidateIDs = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
    }
}

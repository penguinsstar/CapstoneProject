% CalibrationMode = 0 : mPTP
% CalibrationMode = 1 : fPTP


function [DBP] = Calculate_DBP(SBP0, DBP0, PTT0, fPTT0, fDBP0, ECG, PPG, gamma, CalibrationMode)

    PTTcurrent = Calculate_PTT(ECG, PPG);
    
    if(~CalibrationMode)

        % BP estimation with mPTP parameters only
        DBP = (SBP0 + 2*DBP0)/3 + ((2/gamma)*log(PTT0/PTTcurrent)) - ((SBP0 - DBP0)/3)*((PTT0/PTTcurrent)^2);
    
    else

        % BP estimation with fPTP parameters
        % TODO: remove one of these 2 after testing
        DBP = (SBP0 + 2*fDBP0)/3 + ((2/gamma)*log(fPTT0/PTTcurrent)) - ((SBP0 - fDBP0)/3)*((fPTT0/PTTcurrent)^2);
    end
end


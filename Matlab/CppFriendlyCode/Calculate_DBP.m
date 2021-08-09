function [DBP] = Calculate_DBP(SBP0, DBP0, PTT0, PTTcurrent, gamma)

   
    % BP estimation with mPTP parameters only
    DBP = (SBP0 + 2*DBP0)/3 + ((2/gamma)*log(PTT0/PTTcurrent)) - ((SBP0 - DBP0)/3)*((PTT0/PTTcurrent)^2);
    
   
end


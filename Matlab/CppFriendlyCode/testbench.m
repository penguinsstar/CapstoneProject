T = readtable('sensor-data.xlsx', 'Sheet', 'Sheet3');
ECG=normalize(T.('ECG'));
PPG=normalize(T.('PPG'));

ECGfull = [ECG+rand(1000,1) ECG+rand(1000,1) ECG+rand(1000,1) ECG+rand(1000,1) ECG+rand(1000,1)];
PPGfull = [PPG+rand(1000,1) PPG+rand(1000,1) PPG+rand(1000,1) PPG+rand(1000,1) PPG+rand(1000,1)];
RealDBP = [64 66 66 67 68];
RealSBP = [94 96 96 97 98];
gamma = 0.031;

% [SBP0, DBP0, PTT0, fPTT0, fDBP0] = Calibrate(ECG, PPG, RealDBP, RealSBP, gamma, CalibrationMode)

% [DBP] = Calculate_DBP(SBP0, DBP0, PTT0, fPTT0, fDBP0, ECG, PPG, gamma, CalibrationMode)

[SBP0, DBP0, PTT0] = Calibrate(ECGfull, PPGfull, RealDBP, RealSBP)

[DBP] = Calculate_DBP(114.8, 66.4, 0.1, ECG, PPG, gamma)



% extra data export
allData = [ECGfull ECG PPGfull PPG ];
filename = 'testdata.xlsx';
writematrix(allData,filename,'Sheet',1,'Range','A1:V1000')
writematrix(RealDBP.',filename,'Sheet',1,'Range','W1:W5')
writematrix(RealSBP.',filename,'Sheet',1,'Range','X1:X5')

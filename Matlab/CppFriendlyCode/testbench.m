T = readtable('testdataFull.xlsx', 'Sheet', '30s_ECG_PPG');
ECG=(T.('ECG'));
PPG=(T.('PPG'));

ECGfull = [ECG(1:1000) ECG(1001:2000) ECG(2001:3000) ECG(3001:4000) ECG(4001:5000)];
PPGfull = [PPG(1:1000) PPG(1001:2000) PPG(2001:3000) PPG(3001:4000) PPG(4001:5000)];
RealDBP = [64 66 66 67 68];
RealSBP = [94 96 96 97 98];
gamma = 0.031;

% [SBP0, DBP0, PTT0, fPTT0, fDBP0] = Calibrate(ECG, PPG, RealDBP, RealSBP)

% [DBP] = Calculate_DBP(SBP0, DBP0, PTT0, ECG, PPG, gamma)

[SBP0, DBP0, PTT0] = Calibrate(ECGfull, PPGfull, RealDBP, RealSBP)

% 114.8, 66.4, 0.1
[DBP] = Calculate_DBP(SBP0, DBP0, PTT0, ECG(1:1000), PPG(1:1000), gamma)



% extra data export
allData = [ECGfull PPGfull];
filename = 'testdata.xlsx';
writematrix(allData,filename,'Sheet',1,'Range','A1:J1000')


% T = readtable('sensor-data.xlsx', 'Sheet', 'Sheet3');
% ECG=normalize(T.('ECG'));
% PPG=normalize(T.('PPG'));
% 
% ECGfull = [ECG+rand(1000,1) ECG+rand(1000,1) ECG+rand(1000,1) ECG+rand(1000,1) ECG+rand(1000,1)];
% PPGfull = [PPG+rand(1000,1) PPG+rand(1000,1) PPG+rand(1000,1) PPG+rand(1000,1) PPG+rand(1000,1)];


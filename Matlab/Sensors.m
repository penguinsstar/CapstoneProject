%% Variables
lowPassFc = 20;
SCGlowPassFc = 22;
ECGlowPassFc = 40;
highPassFc = 0.3;
SamplingFrequency = 100;
butterworthOrder = 2;
smoothingWindowSize = 20;
baudRate = 19200;

%% Configure Arduino Serial Port
arduinoObj = serialport('COM7',baudRate);
configureTerminator(arduinoObj,"CR/LF");
flush(arduinoObj);
arduinoObj.UserData.Data = struct("PPG",[], "ECG", [], "SCG", [],"NormPPG", [], "NormECG", [], "NormSCG", [], "Count", 1);

configureCallback(arduinoObj,"terminator",@readSensorData);

% wait until data is collected
while(~(arduinoObj.BytesAvailableFcnMode == "off"))
    pause(1); %% wait for a second
end

%% Apply high pass filter
PPGdata = arduinoObj.UserData.Data.PPG;
ECGdata = arduinoObj.UserData.Data.ECG;
HpFilteredData = highpass(PPGdata, highPassFc, SamplingFrequency);
HpFilteredDataECG = highpass(ECGdata, highPassFc, SamplingFrequency);
% figure(2);
% title('After HPF');
% plot(HpFilteredData);

%% Design and apply low pass filter
SCGdata = arduinoObj.UserData.Data.SCG;
digitalLPF = designfilt('lowpassiir', 'FilterOrder', butterworthOrder, 'HalfPowerFrequency', lowPassFc/(SamplingFrequency/2), 'DesignMethod', 'butter');
ECGdigitalLPF = designfilt('lowpassiir', 'FilterOrder', butterworthOrder, 'HalfPowerFrequency', ECGlowPassFc/(SamplingFrequency/2), 'DesignMethod', 'butter');
% SCGdigitalLPF = designfilt('lowpassiir', 'FilterOrder', 1, 'HalfPowerFrequency', SCGlowPassFc/(SamplingFrequency/2), 'DesignMethod', 'butter');
LpFilteredData = filter(digitalLPF, HpFilteredData);
LpFilteredDataECG = filter(ECGdigitalLPF, HpFilteredDataECG);
LpFilteredDataSCG = lowpass(SCGdata, SCGlowPassFc, SamplingFrequency);
figure(3);
hold on;
title('After LPF');
plot(normalize(LpFilteredData));
plot(normalize(LpFilteredDataECG));  
plot(normalize(LpFilteredDataSCG));
legend('PPG', 'ECG', 'SCG');
hold off;

%% Moving Average
movingAverageWindowSize = 40;
averagedData = movmean(LpFilteredData, movingAverageWindowSize);
smoothedData = smoothdata(LpFilteredData);
ECGsmoothedData = smoothdata(LpFilteredDataECG);
SCGsmoothedData = smoothdata(LpFilteredDataSCG);
figure(4);
hold on;
title('Heuristically Smoothed Data');
plot(normalize(smoothedData));
plot(normalize(LpFilteredDataECG));
plot(normalize(LpFilteredDataSCG));
legend('PPG', 'ECG');
hold off;

% figure(5);
% title('Moving Average (Window = 2 beats)');
% plot(averagedData);





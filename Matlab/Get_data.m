function [ECG, PPG] = Get_data()
clc;
clear arduinoObj;
close all;
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
pause(5);
disp("starting data collection...");
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
PPGdata = arduinoObj.UserData.Data.PPG(200:end);
ECGdata = arduinoObj.UserData.Data.ECG(200:end);
HpFilteredData = highpass(PPGdata, highPassFc, SamplingFrequency);
HpFilteredDataECG = highpass(ECGdata, highPassFc, SamplingFrequency);


%% Design and apply low pass filter
digitalLPF = designfilt('lowpassiir', 'FilterOrder', butterworthOrder, 'HalfPowerFrequency', lowPassFc/(SamplingFrequency/2), 'DesignMethod', 'butter');
ECGdigitalLPF = designfilt('lowpassiir', 'FilterOrder', butterworthOrder, 'HalfPowerFrequency', ECGlowPassFc/(SamplingFrequency/2), 'DesignMethod', 'butter');
LpFilteredData = filter(digitalLPF, HpFilteredData);
LpFilteredDataECG = filter(ECGdigitalLPF, HpFilteredDataECG);
figure(3);
hold on;
title('After LPF');
plot(normalize(LpFilteredData));
plot(normalize(LpFilteredDataECG));  
legend('PPG', 'ECG', 'SCG');
hold off;

%% Moving Average
movingAverageWindowSize = 40;
averagedData = movmean(LpFilteredData, movingAverageWindowSize);
smoothedData = smoothdata(LpFilteredData);
ECGsmoothedData = smoothdata(LpFilteredDataECG);
figure(4);
hold on;
title('Heuristically Smoothed Data');
plot(normalize(smoothedData));
plot(normalize(LpFilteredDataECG));
legend('PPG', 'ECG');
hold off;
ECG = normalize(LpFilteredDataECG);
PPG = normalize(smoothedData);


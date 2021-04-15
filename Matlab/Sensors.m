%% Variables
lowPassFc = 20;
highPassFc = 0.3;
SamplingFrequency = 200;
butterworthOrder = 2;
smoothingWindowSize = 20;
baudRate = 9600;

%% Configure Arduino Serial Port
% arduinoObj = serialport('COM7',baudRate);
configureTerminator(arduinoObj,"CR/LF");
flush(arduinoObj);
arduinoObj.UserData.Data = struct("PPG",[], "ECG", [], "SCG", [],"NormPPG", [], "NormECG", [], "NormSCG", [], "Count", 1);

configureCallback(arduinoObj,"terminator",@readSensorData);

% wait until data is collected
while(~(arduinoObj.BytesAvailableFcnMode == "off"))
    pause(1); %% wait for a second
end

%% Apply high pass filter
PPGdata = arduinoObj.UserData.Data.NormPPG;
HpFilteredData = highpass(PPGdata, highPassFc, SamplingFrequency);
% figure(2);
% title('After HPF');
% plot(HpFilteredData);

%% Design and apply low pass filter
digitalLPF = designfilt('lowpassiir', 'FilterOrder', butterworthOrder, 'HalfPowerFrequency', lowPassFc/(SamplingFrequency/2), 'DesignMethod', 'butter');
LpFilteredData = filter(digitalLPF, HpFilteredData);
figure(3);
hold on;
title('After LPF');
plot(LpFilteredData);
plot(arduinoObj.UserData.Data.NormECG);  
plot(arduinoObj.UserData.Data.NormSCG);
legend('PPG', 'ECG', 'SCG');
hold off;

%% Moving Average
ovingAverageWindowSize = 40;
averagedData = movmean(LpFilteredData, movingAverageWindowSize);
smoothedData = smoothdata(LpFilteredData);
% figure(4);
% title('Heuristically Smoothed Data');
% plot(smoothedData);

% figure(5);
% title('Moving Average (Window = 2 beats)');
% plot(averagedData);





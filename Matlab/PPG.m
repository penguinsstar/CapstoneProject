
%% Variables
lowPassFc = 20;
highPassFc = 0.3;
SamplingFrequency = 200;
butterworthOrder = 2;
smoothingWindowSize = 20;

%% Configure Arduino Serial Port
% arduinoObj = serialport('COM7',9600);
configureTerminator(arduinoObj,"CR/LF");
flush(arduinoObj);
arduinoObj.UserData = struct("Data",[],"Count",1);
configureCallback(arduinoObj,"terminator",@readSensorData);
% wait until data is collected
pause(5);

%% Apply high pass filter 
HpFilteredData = highpass(arduinoObj.UserData.Data, highPassFc, SamplingFrequency);
figure(2);
title('After HPF');
plot(HpFilteredData);

%% Design and apply low pass filter
digitalLPF = designfilt('lowpassiir', 'FilterOrder', butterworthOrder, 'HalfPowerFrequency', lowPassFc/(SamplingFrequency/2), 'DesignMethod', 'butter');
LpFilteredData = filter(digitalLPF, HpFilteredData);
figure(3);
title('After LPF');
plot(LpFilteredData);

%% Moving Average
movingAverageWindowSize = 10*SamplingFrequency; %% about 10 cardiac cycles assuming 1 beat/sec
averagedFileteredData = movmean(LpFilteredData, movingAverageWindowSize);
figure(4);
title('After moving average');
plot(averagedFilteredData);





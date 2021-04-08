%Define registers


%Get arduino device
%a = arduino()

%Get I2C device
% addrs = scanI2CBus(a);
% ppg_sensor = device(a, 'I2CAddress', '0x57')

% arduinoObj = serialport('COM7',9600);
configureTerminator(arduinoObj,"CR/LF");
flush(arduinoObj);
arduinoObj.UserData = struct("Data",[],"Count",1);
configureCallback(arduinoObj,"terminator",@readSensorData);
pause(5);
hp_data = highpass(arduinoObj.UserData.Data, 0.3, 200);
figure(2);
plot(hp_data);

fc = 20;
fs = 200;
ord = 2;

d_lpf = designfilt('lowpassiir', 'FilterOrder', ord, 'HalfPowerFrequency', fc/(fs/2), 'DesignMethod', 'butter');

lp_data = filter(d_lpf, hp_data);
figure(3);
plot(lp_data);





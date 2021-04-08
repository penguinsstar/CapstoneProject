

% arduinoObj = serialport('COM7',9600);
configureTerminator(arduinoObj,"CR/LF");
flush(arduinoObj);
arduinoObj.UserData = struct("Data",[],"Count",1);
configureCallback(arduinoObj,"terminator",@readSensorData);
pause(5);
hp_data = highpass(arduinoObj.UserData.Data, 0.3, 200);
figure(2);
plot(hp_data);



d_lpf = designfilt('lowpassiir', 'FilterOrder', ord, 'HalfPowerFrequency', fc/(fs/2), 'DesignMethod', 'butter');

lp_data = filter(d_lpf, hp_data);
figure(3);
plot(lp_data);





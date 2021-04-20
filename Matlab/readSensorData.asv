function readSensorData(src, ~)

    % Read the ASCII data from the serialport object.
    data = readline(src);
    data = split(data);

    % Convert the string data to numeric type and save it in the UserData
    % property of the serialport object.
    src.UserData.Data.PPG(end+1) = str2double(data(2));
    src.UserData.Data.ECG(end+1) = str2double(data(1));

    % Update the Count value of the serialport object.
    src.UserData.Data.Count = src.UserData.Data.Count + 1;

    % If 1001 data points have been collected from the Arduino, switch off the
    % callbacks and plot the data.
    if src.UserData.Data.Count > 1000
        configureCallback(src, "off");
        figure(1);
        src.UserData.Data.NormPPG = normalize(src.UserData.Data.PPG(2:end));
        src.UserData.Data.NormECG = normalize(src.UserData.Data.ECG(2:end));
     
        hold on;
        plot(src.UserData.Data.NormPPG);
        plot(src.UserData.Data.NormECG);  
        title('Raw normalized PPG and ECG data');
        legend('PPG', 'ECG', 'SCG');
        hold off;
    end
end
%% ORIGINAL CODE BY ABHIJITH BAILUR, modified by Frank Li, Daniel Wan & Khalil Ammar
function Calculate_DBP(ECG, PPG, isCalibrated, b1, b2, sampling_rate)
% T = readtable('sensor-data.xlsx', 'Sheet', 'Sheet2');


%% data to calibrate
% DBP1=x(1,3);
% DBP2=x(2,3);
% DBP3=x(3,3);
% SBP1=x(1,4);
% SBP2=x(2,4);
% SBP3=x(3,4);

%% ECG signal
% y=normalize(T.('Pre_processedECG')); % ECG signal
y = ECG;
figure,plot(y,'b');
title('ECG signal');
xlabel('time');
ylabel('amplitude');
hold on
%% PPG signal
% z=normalize(T.('Pre_processedPPG')); % PPG signal
z = PPG;
plot(z,'r');
title('PPG signal');
xlabel('time');
ylabel('amplitude');

%% peak detection of ECG
j=1;
n=length(y);
for i=2:n-1
    if y(i)> y(i-1) && y(i)>= y(i+1) && y(i)> 0.45*max(y)
       val(j)= y(i);
       pos(j)=i;
       j=j+1;
     end
end
ecg_peaks=j-1;
ecg_pos=pos./sampling_rate;
plot(pos,val,'*r');
title('ECG peak');
%% peak detection of PPG
m=1;
n=length(z);
zM=movmean(z,6);
for i=2:n-1
    if z(i) > z(i-1) && z(i) >= z(i+1) && z(i) > (zM(i+1))
       val1(m)= z(i);
       pos1(m)=i;
       m=m+1;
    end
end
ppg_peaks=m-1;
ppg_pos=pos1./sampling_rate;
ppg_val=val1;
plot(pos1,val1,'*g');
title('PPG peak');
title('ECG & PPG signal');
legend('ECG signal','PPG signal');


%% PTT
ptt = [];
ppg_ptr = 1;
for i = 1:ecg_peaks
    for j = ppg_ptr:ppg_peaks
        if(ppg_pos(j) > ecg_pos(i))
            ptt(end+1) = ppg_pos(j) - ecg_pos(i);
            ppg_ptr = j;
            break;
        end   
    end  
end
figure,stairs(ptt);
title('PTT');
xlabel('ptt');
ylabel('time');

%% calculate parameters
if (~isCalibrated)
    syms b1 b2
    DBP1 = 71;
    DBP2 = 75;
    mean_ptt_1 = mean(ptt(1:5));
    mean_ptt_2 = mean(ptt(6:11));
    ptt_eqns = [b1/mean_ptt_1 + b2 == 71, b1/mean_ptt_2 + b2 == 75];
    ptt_vars = solve(ptt_eqns,[b1 b2]);
    b1 = ptt_vars.b1;
    b2 = ptt_vars.b2;
end
%% calculate BP and PP
DBP=(b1/mean(ptt))+b2;
disp('Your Diastolic Blood Pressure Is: ');
disp(DBP);
close all;
clear all;
data = readtable('sensor-data.xlsx', 'Sheet', 'Sheet3');
ECG = data.('ECG');
%% peak detection of ECG
y = ECG;
sampling_rate = 100;
figure,plot(y,'b');
title('ECG signal');
xlabel('time');
ylabel('amplitude');
hold on

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
title('ECG peaks');

%% split waveforms into single beats
window_size = 40;
ensembles = zeros(40,length(pos));
for i=1:length(pos)
    l_limit = max(1, pos(i) - window_size/2);
    if l_limit == 1
        u_limit = window_size;
    else
        u_limit = min(length(ECG), pos(i) + window_size/2 -1);
        if u_limit == length(ECG)
            l_limit = length(ECG) - window_size + 1;
        end
    end
    ensembles(:,i) = ECG(l_limit:u_limit);
end

figure;
plot(ensembles)
title('ECG ensembles');
averaged_ECG = mean(ensembles, 2);
figure;
plot(averaged_ECG);
title('ensemble-averaged ECG signal');
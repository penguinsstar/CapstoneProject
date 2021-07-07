function [ptt] = Calculate_PTT(debug)
if(~debug)
    [ECG, PPG] = Get_data();
end
%% Debug parameters
T = readtable('sensor-data.xlsx', 'Sheet', 'Sheet3');
sampling_rate = 100;

%% ECG signal
if(debug)
    ECG=normalize(T.('ECG')); % ECG signal
end
figure,plot(ECG,'b');
title('ECG signal');
xlabel('time');
ylabel('amplitude');
hold on

%% PPG signal
if(debug)
    PPG=normalize(T.('PPG')); % PPG signal
end
plot(PPG,'r');
title('PPG signal');
xlabel('time');
ylabel('amplitude');

%% peak detection of ECG
plot(ECG,'b');
title('ECG signal');
xlabel('time');
ylabel('amplitude');
hold on

j=1;
n=length(ECG);
for i=2:n-1
    if ECG(i)> ECG(i-1) && ECG(i)>= ECG(i+1) && ECG(i)> 0.45*max(ECG)
       val(j)= ECG(i);
       ECG_peaks(j)=i;
       j=j+1;
     end
end
plot(ECG_peaks,val,'*r');
title('ECG peak');

%% peak detection of PPG
[PPG_peaks,~] = peakdet(PPG, 0.5);

plot(PPG_peaks(:,1),PPG_peaks(:,2),'*g');
title('PPG peak');
title('ECG & PPG signal');
legend('ECG signal','PPG signal');

%% split waveforms into single beats
window_size = floor(mean(diff(ECG_peaks))); % window size defined as the average distance between peaks
if mod(window_size,2) ~= 0
    window_size = window_size - 1;
end

ECG_ensembles = zeros(window_size,length(ECG_peaks)-1);
PPG_ensembles = zeros(window_size, length(ECG_peaks)-1);

% split ECG waveform around peaks
for i=2:length(ECG_peaks)
    l_limit = max(1, ECG_peaks(i) - window_size/2); %ensure minimum index = 1
    if l_limit == 1
        u_limit = window_size;
    else
        u_limit = min(length(ECG), ECG_peaks(i) + window_size/2 -1); %ensure maximum index = length(ECG)
        if u_limit == length(ECG)
            l_limit = length(ECG) - window_size + 1;
        end
    end
    ECG_ensembles(:,i-1) = ECG(l_limit:u_limit);
    PPG_ensembles(:,i-1) = PPG(l_limit:u_limit);
end


%% Calculate & Plot Ensemble-Averaged Signals
figure;
plot(ECG_ensembles);
title('ECG ensembles');
figure;
plot(PPG_ensembles);
title('PPG ensembles');

% calculate ensemble average for ECG and PPG signals
ens_avg_ECG = mean(ECG_ensembles, 2);
ens_avg_PPG = mean(PPG_ensembles, 2);
[~, PPG_max_idx] = max(ens_avg_PPG);
[~, ECG_max_idx] = max(ens_avg_ECG);
ptt = (max(PPG_max_idx) - max(ECG_max_idx))/sampling_rate; 

figure;
hold on;
plot(ens_avg_ECG);
plot(ens_avg_PPG);
hold off;
title('ensemble-averaged ECG and PPG signals');
legend('ECG', 'PPG');
end
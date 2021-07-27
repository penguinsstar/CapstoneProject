function [ptt] = Calculate_PTT(ECG, PPG)

sampling_rate = 100;

j=1;
n=length(ECG);
ECG_peaks_initial = zeros(length(ECG)-1);
for i=2:n-1
    if ECG(i)> ECG(i-1) && ECG(i)>= ECG(i+1) && ECG(i)> 0.45*max(ECG)
       ECG_peaks_initial(j)=i;
       j=j+1;
     end
end
ECG_peaks = ECG_peaks_initial(1:j-1);


%% split waveforms into single beats
window_size = floor(mean(diff(ECG_peaks))); % window size defined as the average distance between peaks
if mod(window_size,2) ~= 0
    window_size = window_size - 1;
end

ECG_ensembles = zeros(window_size, length(ECG_peaks)-1);
PPG_ensembles = zeros(window_size, length(ECG_peaks)-1);

% split ECG waveform around peaks
for i=2:length(ECG_peaks)
    l_limit = max(1, ECG_peaks(i) - window_size/2); %ensure minimum index = 1
    if l_limit == 1
        u_limit = window_size;
    else
        u_limit = min(length(ECG), ECG_peaks(i) + window_size/2 - 1); %ensure maximum index = length(ECG)
        if u_limit == length(ECG)
            l_limit = length(ECG) - window_size + 1;
        end
    end
    ECG_ensembles(:,i-1) = ECG(l_limit:u_limit);
    PPG_ensembles(:,i-1) = PPG(l_limit:u_limit);
end

% calculate ensemble average for ECG and PPG signals
ens_avg_ECG = mean(ECG_ensembles, 2);
ens_avg_PPG = mean(PPG_ensembles, 2);
[~, PPG_max_idx] = max(ens_avg_PPG);
[~, ECG_max_idx] = max(ens_avg_ECG);
ptt = (PPG_max_idx - ECG_max_idx)/sampling_rate; 

end
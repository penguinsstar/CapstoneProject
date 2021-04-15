%% BASIC CODE BY ABHIJITH BAILUR, modified by Frank Li
clc;
clear all;
close all;
x=load('day2_0917.txt');
%% data to calibrate
DBP1=x(1,3);
DBP2=x(2,3);
DBP3=x(3,3);
SBP1=x(1,4);
SBP2=x(2,4);
SBP3=x(3,4);
%% ECG signal
y=x(1:95000,1); % ECG signal
figure,plot(y);
title('ECG signal');
xlabel('time');
ylabel('amplitude');
hold on

%% PPG signal
z=x(1:95000,2); % PPG signal
plot(z,'r');
title('PPG signal');
xlabel('time');
ylabel('amplitude');
%% SCG signal
s=x(1:95000,3); % PPG signal
plot(s,'r');
title('SCG signal');
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
ecg_pos=pos./1000;
plot(pos,val,'*r');
title('ECG peak');
%% minus peak detection of PPG
m=1;
n=length(z);
for i=2:n-1 && m<j
    if z(i)< z(i-1) && z(i)<= z(i+1) && z(i)< 0.45*min(z)
       val(m)= z(i);
       pos1(m)=i;
       m=m+1;
     end
end
ppg_peaks=m-1;
ppg_pos=pos1./1000;
ppg_val=val;
plot(pos1,val,'*g');
title('PPG peak');
%% peak detection of SCG
q=1;
n=length(s);
for i=2:n-1 && q<j
    if s(i)> s(i-1) && s(i)>= s(i+1) && s(i)> 0.45*max(s)
       val(q)= s(i);
       pos2(q)=i;
       q=q+1;
     end
end
scg_peaks=q-1;
scg_pos=pos2./1000;
scg_val=val;
plot(pos2,val,'*b');
title('ECG & PPG & SCG signal');
legend('ECG signal','PPG signal','SCG signal');
% %% HRV
% j=1;
% for i=1:ecg_peaks-1
%     e(j)= ecg_pos(i+1)-ecg_pos(i);% gives RR interval
%     j=j+1;
%     
% end 
% hr=60./mean(e); % 60/ mean of RR interval
% 
% hrv= (60./e); % 60/ each RR interval
% figure,stairs(hrv);
% title('HRV');
% xlabel('samples');
% ylabel('hrv');
% 
% %% PRV
% k=1;
% for i=1:ppg_peaks-1
%     f(k)= ppg_pos(i+1)-ppg_pos(i); 
%     k=k+1;
% end 
% pr=60./mean(f); 
% prv= 60./f; 
% figure,stairs(prv);
% title('PRV');
% xlabel('samples');
% ylabel('prv');
%% PTT
ptt=(ppg_pos-scg_pos);
figure,stairs(ptt);
title('PTT');
xlabel('ptt');
ylabel('time');
%% PEP
pep=(scg_pos-ecg_pos);
figure,stairs(pep);
title('PEP');
xlabel('pep');
ylabel('time');

%% calculate parameters
[a1,a2,a3]=solve('(a1*pep(0)/(ptt(0)^2))+(a2/(ptt(0)^2))+a3=(SBP1-DBP1)','(a1*pep(1)/(ptt(1)^2))+(a2/(ptt(1)^2))+a3=(SBP2-DBP2)','(a1*pep(2)/(ptt(2)^2))+(a2/(ptt(2)^2))+a3=(SBP3-DBP3)','a1,a2,a3');
[b1,b2]=solve('(b1/ptt(0))+b2=DBP1','(b1/ptt(1))+b2=DBP2','b1,b2');
%% calculate BP and PP
PP=(a1.*pep./(ptt.^2))+(a2./(ptt.^2))+a3;
figure,plot(PP);
title('PP');
xlabel('amplitude');
ylabel('time');
hold on;
DBP=(b1./ptt)+b2;
figure,stairs(DBP);
title('DBP');
xlabel('amplitude');
ylabel('time');
SBP=DBP+PP;
title('SBP');
xlabel('amplitude');
ylabel('time');
title('PP & DBP & SBP signal');
legend('PP signal','DBP signal','SBP signal');
%% notch detection

%%moving average filter
av=smooth(z,150);

%%differentiation
p=100*diff(av,1); % (signal,order of differentiation), 100 to amplify the signal

%%finding peak of the notch on the differentiated signal
np=1;  % notch peak
m=length(p); 
for i=2:m-1
    if p(i)> p(i-1) && p(i)>= p(i+1) 
       val(np)= p(i);
       pos(np)=i;
       np=np+1;
    end
end
u=1;
for j=1:2:length(pos) 
    notch_pos(u)=pos(j);
    notch_val(u)=val(j);
    u=u+1;
end

n_val=z(notch_pos);

%% reflection index = b/a *100
%b=diff between notch and peak in y axis
%a=ppg peak value in y axis
nv=n_val(2,:)';
ri=((ppg_val-nv)./ppg_val)*100;
ref_index=mean(ri)

%% stiffness index = h/ptt; 
h=0.60;%h is the length from subject's finger tip to heart
si=(h./ptt);
stif_index=mean(si)


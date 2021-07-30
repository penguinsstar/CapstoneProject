function [ECG, PPG] = Data_Processing(ECG, PPG)
    

    %% Variables
    %lowPassFc = 20;
    %ECGlowPassFc = 40;
    %highPassFc = 1;
    %SamplingFrequency = 100;
    %butterworthOrder = 2;


    %% Apply high pass filter
    ECGdata = ECG;
    PPGdata = PPG;
    
    %[HpFilteredDataECG, d_ECG] = highpass(ECGdata, highPassFc, SamplingFrequency);      
    sos = [1.35016644305373,-2.69722464286958,1.35016644305373,1,-1.98006187048069,0.984367933575918;
        2.56602036106036,-5.12874729086195,2.56602036106036,1,-1.94242229086518,0.948634277922819;
        14.0370808794101,-28.0715120966529,14.0370808794101,1,-1.80818990372137,0.821368999504786;
        0.0177622969486616,-0.0354751970775001,0.0177622969486616,1,-1.99253171983125,0.996332358206565];
    [b,a] = sos2tf(sos);
    HpFilteredDataECG = filtfilt(b,a,ECGdata);
    
    %[HpFilteredDataPPG, d_PPG] = highpass(PPGdata, highPassFc, SamplingFrequency);     
    sos = [1.35016644305373,-2.69722464286958,1.35016644305373,1,-1.98006187048069,0.984367933575918;
        2.56602036106036,-5.12874729086195,2.56602036106036,1,-1.94242229086518,0.948634277922819;
        14.0370808794101,-28.0715120966529,14.0370808794101,1,-1.80818990372137,0.821368999504786;
        0.0177622969486616,-0.0354751970775001,0.0177622969486616,1,-1.99253171983125,0.996332358206565];
    [b,a] = sos2tf(sos);
    HpFilteredDataPPG = filtfilt(b,a,PPGdata);

    
    %% Design and apply low pass filter

    %ECGdigitalLPF = designfilt('lowpassiir', 'FilterOrder', butterworthOrder, 'HalfPowerFrequency', ECGlowPassFc/(SamplingFrequency/2), 'DesignMethod', 'butter');
    %LpFilteredDataECG = filter(ECGdigitalLPF, HpFilteredDataECG);
    sos = [0.638945525159022,1.27789105031804,0.638945525159022,1,1.14298050253990,0.412801598096189];
    [b,a] = sos2tf(sos);
    iir = dsp.IIRFilter('Numerator',b,'Denominator',a);
    LpFilteredDataECG = iir(HpFilteredDataECG(1:1000));
    
    %PPGdigitalLPF = designfilt('lowpassiir', 'FilterOrder', butterworthOrder, 'HalfPowerFrequency', lowPassFc/(SamplingFrequency/2), 'DesignMethod', 'butter');
    %LpFilteredDataPPG = filter(PPGdigitalLPF, HpFilteredDataPPG);
    sos = [0.206572083826148,0.413144167652296,0.206572083826148,1,-0.369527377351241,0.195815712655833];
    [b,a] = sos2tf(sos);
    iir = dsp.IIRFilter('Numerator',b,'Denominator',a);
    LpFilteredDataPPG = iir(HpFilteredDataPPG(1:1000));
    

    %% Moving Average  
    ECG = normalize(LpFilteredDataECG);
    PPG = normalize(smoothdata(LpFilteredDataPPG));


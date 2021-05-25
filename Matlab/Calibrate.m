function [m, b] = Calibrate(PTT,DBP)
paddedPTT = [ones(length(PTT),1) PTT];
linearRegressionCoeff = paddedPTT\DBP;  %% Perform linear regression on the input data
b = linearRegressionCoeff(1);
m = linearRegressionCoeff(2);
testRegression = m.*PTT + b;
figure;
hold on;
scatter(PTT, DBP);
plot(PTT, testRegression);
hold off;
end
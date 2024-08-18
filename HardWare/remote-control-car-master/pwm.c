#include "pwm.h"

// STC8��Ƭ���ϵ�ÿ�����Ŷ��������PWM����

// ����ο���https://blog.csdn.net/chuncongrun3534/article/details/113931300
// https://www.stcmicro.com/datasheet/stc8g1k08.pdf  17.2.4.4 10 λPWMģʽ
/*
������pwm���
P3.5 P3.6 P3.7 P1.0 P1.1
*/
#define PWM_CHAN  0x3u

void pwm_init(void)
{
	CL = 0x00;   // PCA �������Ĵ����� һ��PCA ticks��һ��
	CH = 0x00;
	// PCA ģʽ�Ĵ���������ģʽ�� PCA ֹͣ������PCA����ʱ��ԴΪϵͳʱ��/12��Ƶ�ʲ��ɱ䣩����ֹPCA����������ж�
	// ע�⣺����Ҫ�ı�Ƶ�ʣ���Ҫѡ��ʱ����Ϊʱ��Դ���롣
	CMOD = 0x80; 
	
	// PCA ģ��ģʽ���ƼĴ�����PCAģ��2ΪPWM����ģʽ��
	CCAPM2 = 0x42;
	CCAPM0 = 0x42;
	CCAPM1 = 0x42;	
	// PCA ģ�� PWM ģʽ���ƼĴ�����PCAģ��2���10λPWM������ֵ�� CCAP2H; 010λ�Ƚ�ֵ��CCAP2L
	// 8λPWM�����һ�����ڹ̶�Ϊ2^8ticks��Ƶ��Ϊ11.0592MHz/12/256 = 3.6KHz
	PCA_PWM2 = 0x3;   // EPC2L = 1, EPC2H = 1, ���ڿ���������͵�ƽ
	PCA_PWM0 = 0x3;
	PCA_PWM1 = 0x3;
	
	// ����ռ�ձȣ�CL[7:0] < EPCnL, CCAP2Lʱ������͵�ƽ;CL[7:0] >= EPCnL, CCAP2Lʱ������ߵ�ƽ;
	// CH[1:0]+CL[7:0]���ʱ��2^10��ticks������CCAP2H���ص�CCAP2L�С���CCAP2Lû���������
	CCAP2H = 0xFF;  // PCA ģ��ģʽ����ֵ/�Ƚ�ֵ�Ĵ���
	CCAP2L = 0xFF;  // PCA ģ��ģʽ����ֵ/�Ƚ�ֵ�Ĵ���
	CCAP0H = 0xFF;
	CCAP0L = 0xFF;
	CCAP1H = 0xFF;
	CCAP1L = 0xFF;
	
	P_SW1 &= 0xCF;   // CCP0:P1.1   CCP1:P1.0   CCP2:P3.7
	
	// P3����Ϊ׼˫���
	P3M0 = 0x00;
	P3M1 = 0x00;
	P1M0 = 0x00;
	P1M1 = 0x00;
	
	CR = 1; // ����PCA��ʱ��
}

// ���ȣ�1/256 = 0.39%
void pwm_write(u8 ch, u8 duty)
{
	if(ch == PWM_CH0_PIN11){
		if (duty == 0){
			PCA_PWM0 = 0x3;
		}
		else {
			PCA_PWM0 = 0x0;
			CCAP0H = 0xFF - duty;
			CCAP0L = 0xFF - duty;
		}
	}
	else if(ch == PWM_CH1_PIN10){
		if (duty == 0){
			PCA_PWM1 = 0x3;
		}
		else {
			PCA_PWM1 = 0x0;
			CCAP1H = 0xFF - duty;
			CCAP1L = 0xFF - duty;
		}
	}
	else if(ch == PWM_CH2_PIN37){
		if (duty == 0){
			PCA_PWM2 = 0x3;
		}
		else {
			PCA_PWM2 = 0x0;
			CCAP2H = 0xFF - duty;
			CCAP2L = 0xFF - duty;
		}
	}
	else
	{
		// ch error
	}
}
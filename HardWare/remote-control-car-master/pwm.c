#include "pwm.h"

// STC8单片机上的每个引脚都可以输出PWM波形

// 编码参考：https://blog.csdn.net/chuncongrun3534/article/details/113931300
// https://www.stcmicro.com/datasheet/stc8g1k08.pdf  17.2.4.4 10 位PWM模式
/*
可用作pwm输出
P3.5 P3.6 P3.7 P1.0 P1.1
*/
#define PWM_CHAN  0x3u

void pwm_init(void)
{
	CL = 0x00;   // PCA 计数器寄存器， 一次PCA ticks加一次
	CH = 0x00;
	// PCA 模式寄存器：空闲模式下 PCA 停止计数，PCA输入时钟源为系统时钟/12（频率不可变），禁止PCA计数器溢出中断
	// 注意：如需要改变频率，需要选择定时器作为时钟源输入。
	CMOD = 0x80; 
	
	// PCA 模块模式控制寄存器，PCA模块2为PWM工作模式；
	CCAPM2 = 0x42;
	CCAPM0 = 0x42;
	CCAPM1 = 0x42;	
	// PCA 模块 PWM 模式控制寄存器，PCA模块2输出10位PWM；重载值： CCAP2H; 010位比较值：CCAP2L
	// 8位PWM输出，一个周期固定为2^8ticks，频率为11.0592MHz/12/256 = 3.6KHz
	PCA_PWM2 = 0x3;   // EPC2L = 1, EPC2H = 1, 用于控制输出常低电平
	PCA_PWM0 = 0x3;
	PCA_PWM1 = 0x3;
	
	// 控制占空比，CL[7:0] < EPCnL, CCAP2L时，输出低电平;CL[7:0] >= EPCnL, CCAP2L时，输出高电平;
	// CH[1:0]+CL[7:0]溢出时（2^10个ticks），将CCAP2H重载到CCAP2L中。（CCAP2L没变过啊？）
	CCAP2H = 0xFF;  // PCA 模块模式捕获值/比较值寄存器
	CCAP2L = 0xFF;  // PCA 模块模式捕获值/比较值寄存器
	CCAP0H = 0xFF;
	CCAP0L = 0xFF;
	CCAP1H = 0xFF;
	CCAP1L = 0xFF;
	
	P_SW1 &= 0xCF;   // CCP0:P1.1   CCP1:P1.0   CCP2:P3.7
	
	// P3设置为准双向口
	P3M0 = 0x00;
	P3M1 = 0x00;
	P1M0 = 0x00;
	P1M1 = 0x00;
	
	CR = 1; // 启动PCA计时器
}

// 精度：1/256 = 0.39%
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
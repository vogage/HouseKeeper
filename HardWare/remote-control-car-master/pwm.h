#ifndef __PWM_H__
#define __PWM_H__
#include	"config.h"

typedef enum{
	PWM_CH0_PIN11 = 0,
	PWM_CH1_PIN10,
	PWM_CH2_PIN37,
	PWM_CH_ERR
};

void pwm_init(void);

void pwm_write(u8 ch, u8 duty);

#endif
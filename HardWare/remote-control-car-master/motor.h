#ifndef __MOTOR_H__
#define __MOTOR_H__
#include	"config.h"

void motor_init(void);

// ret: 0:执行成功，   非0:参数异常，执行失败
u8 motor_control(u8 forward, u8 back, u8 left, u8 right);

#endif
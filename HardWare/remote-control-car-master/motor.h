#ifndef __MOTOR_H__
#define __MOTOR_H__
#include	"config.h"

void motor_init(void);

// ret: 0:ִ�гɹ���   ��0:�����쳣��ִ��ʧ��
u8 motor_control(u8 forward, u8 back, u8 left, u8 right);

#endif
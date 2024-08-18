#include "motor.h"
#include "pwm.h"

/*
 *��               �� 
 * M1              M2
 * out1/2          out3/4
 * in1/2           in3/4
 * P1.2/P1.0       p1.3/P1.1
 */

void motor_init(void){
	pwm_init();
	P1M0 = 0x00;
	P1M1 = 0x00;
	P3M0 = 0x00;
	P3M1 = 0x00;
}

#define M1DO   P12
#define M1AO   PWM_CH1_PIN10
#define M2DO   P13
#define M2AO   PWM_CH0_PIN11

#define PWM_FULL   					(255U)
#define MOTOR_POWER_CLOCK   (80U)
#define MOTOR_POWER_UNCLOCK (PWM_FULL - MOTOR_POWER_CLOCK)
// ת��/ǰ��ͬʱ����ʱ��ת�����ȡ�
u8 motor_control(u8 forward, u8 back, u8 left, u8 right){
  if(((forward  > 0) && (back >0)) || ((left > 0) && (right > 0))) {
    return 1;
  }
  // ����ת��
  if(left > 0){
    // M1 ��ת�� M2 ��ת
		M1DO = 0;
		pwm_write(M1AO, MOTOR_POWER_CLOCK);

    M1DO = 1;
		pwm_write(M2AO, MOTOR_POWER_CLOCK);
  }
  else if(right > 0){
    // M1 ��ת�� M2 ��ת
		M1DO = 1;
    pwm_write(M1AO, MOTOR_POWER_UNCLOCK);

    M2DO = 0;
    pwm_write(M2AO, MOTOR_POWER_CLOCK);
  }
  // ���ǰ��
  else if(forward > 0) {
    // M1/2 ��ת
    M1DO = 1;
    pwm_write(M1AO, MOTOR_POWER_UNCLOCK);

    M2DO = 1;
    pwm_write(M2AO, MOTOR_POWER_UNCLOCK);
  }
  else if (back > 0){
    // M1/2 ��ת
    M1DO = 0;
    pwm_write(M1AO, MOTOR_POWER_CLOCK);

    M2DO = 0;
    pwm_write(M2AO, MOTOR_POWER_CLOCK);
  }
  else{  // ֹͣ
    M1DO = 0;
    pwm_write(M1AO, 0);

    M2DO = 0;
    pwm_write(M2AO, 0);
  }
  return 0;
}
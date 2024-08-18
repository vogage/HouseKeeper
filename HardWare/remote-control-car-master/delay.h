#ifndef	__DELAY_H
#define	__DELAY_H

#include	"config.h"

//========================================================================
// 函数: void  delay_ms(unsigned char ms)
// 描述: 延时函数。
// 参数: ms,要延时的ms数, 这里只支持1~255ms. 自动适应主时钟.
// 返回: none.
//========================================================================
void  delay_ms(unsigned char ms);

#endif

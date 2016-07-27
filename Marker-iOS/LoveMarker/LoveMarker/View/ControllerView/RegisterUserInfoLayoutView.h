//
//  RegisterUserInfoLayoutView.h
//  LoveMarker
//
//  Created by BigHead_Chen on 6/24/16.
//  Copyright © 2016 Eason. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ClickableUIView.h"

@interface RegisterUserInfoLayoutView : UIView

-(id)initViewContext:(id)context title:(NSString*)title frame:(CGRect)frame;

@property (strong,nonatomic) UIView *genderLayout;
@property (strong,nonatomic) UIView *genderMiddleLineView;

@property (strong,nonatomic) UILabel *maleLabel;
@property (strong,nonatomic) ClickableUIView *maleView;
@property (strong,nonatomic) UIView *maleCheckView;

@property (strong,nonatomic) UILabel *femaleLabel;
@property (strong,nonatomic) ClickableUIView *femaleView;
@property (strong,nonatomic) UIView *femalCheckView;

@property (strong,nonatomic) UIDatePicker* datePicker;
@property (strong,nonatomic) UIButton *nextStepButton;

@end
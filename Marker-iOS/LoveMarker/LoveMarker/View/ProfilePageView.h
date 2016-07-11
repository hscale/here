//
//  ProfilePageView.h
//  LoveMarker
//
//  Created by BigHead_Chen on 7/9/16.
//  Copyright © 2016 Eason. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AvatarUIImageView.h"
#import "UserInforCell.h"
#import "TopLayoutView.h"

@interface ProfilePageView : UIView

@property (strong,nonatomic) TopLayoutView *topLayoutView;
@property (strong,nonatomic) ClickableUIView *avatarUIView;
@property (strong,nonatomic) UILabel *changeAvatarUILabel;
@property (strong,nonatomic) AvatarUIImageView *avatarUIImageView;
@property (strong,nonatomic) UserInforCell *myMarkerUIView;
@property (strong,nonatomic) UserInforCell *nicknameUIView;
@property (strong,nonatomic) UserInforCell *genderUIView;
@property (strong,nonatomic) UserInforCell *birthdayUIView;
@property (strong,nonatomic) ClickableUIView *simpleProfileUIView;
@property (strong,nonatomic) UILabel *simpleProfileUILabel;
@property (strong,nonatomic) UILabel *simpleProfileContentUILabel;
@property (strong,nonatomic) ClickableUIView *longProfileUIView;
@property (strong,nonatomic) UILabel *longProfileUILabel;
@property (strong,nonatomic) UILabel *longProfileContentUILabel;
@property (strong,nonatomic) UserInforCell *usernameUIView;
@property (strong,nonatomic) UserInforCell *passwordUIView;

-(id)initWithContext:(id)context title:(NSString*)topTitle frame:(CGRect)frame;
@end

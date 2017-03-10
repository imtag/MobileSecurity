package com.tfx.mobilesafe.activity;

import java.util.List;

import com.tfx.mobilesafe.dao.ContactsDao;
import com.tfx.mobilesafe.domain.ContactBean;

/**
 * @author    Tfx
 * @comp      GOD
 * @date      2016-7-29
 * @desc      显示通话记录

 * @version   $Rev: 19 $
 * @auther    $Author: tfx $
 * @date      $Date: 2016-07-29 23:17:19 +0800 (星期五, 29 七月 2016) $
 * @id        $Id: CallLogActivity.java 19 2016-07-29 15:17:19Z tfx $
 */

public class CallLogActivity extends BaseSmsTelFriendActivity {

	@Override
	public List<ContactBean> getDatas() {
		return ContactsDao.getCallLog(getApplicationContext());
	}

}

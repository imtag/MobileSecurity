package com.tfx.mobilesafe.activity;

import java.util.List;
import com.tfx.mobilesafe.dao.ContactsDao;
import com.tfx.mobilesafe.domain.ContactBean;

/**
 * @author Tfx
 * @comp   GOD
 * @date   2016-7-24
 * @desc   显示所有联系人的界面
 * 
 * @version $Rev: 19 $
 * @auther  $Author: tfx $
 * @date    $Date: 2016-07-29 23:17:19 +0800 (星期五, 29 七月 2016) $
 * @id      $Id: FriendsActivity.java 19 2016-07-29 15:17:19Z tfx $
 */

public class FriendsActivity extends BaseSmsTelFriendActivity {

	@Override
	public List<ContactBean> getDatas() {
		return ContactsDao.getContacts(getApplicationContext());
	}
}

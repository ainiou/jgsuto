package com.pro.jgsu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.jgsu.entity.AddressBook;
import com.pro.jgsu.mapper.AddressBookMapper;
import com.pro.jgsu.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}

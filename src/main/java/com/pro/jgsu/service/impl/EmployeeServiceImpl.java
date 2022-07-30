package com.pro.jgsu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pro.jgsu.entity.Employee;
import com.pro.jgsu.mapper.EmployeeMapper;
import com.pro.jgsu.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @Author XWH
 * @Time 2022/7/26 下午 22:37
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}

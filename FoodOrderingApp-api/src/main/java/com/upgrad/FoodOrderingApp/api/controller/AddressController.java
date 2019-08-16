package com.upgrad.FoodOrderingApp.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.dao.addressDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
public class AddressController {
    @Autowired
    private CustomerService CSI;
    @Autowired
    private AddressService ASI;
    @Autowired
    private addressDao ad;
    @RequestMapping(value = "/address",method= RequestMethod.POST)
    public ResponseEntity<SaveAddressResponse>saveAddress(@RequestBody(required = false) final SaveAddressRequest s,@RequestHeader("authorization")final String authentication)throws Exception{
            CustomerEntity c=CSI.getCustomer(authentication);
            AddressEntity a=new AddressEntity();
            a.setCity(s.getCity());
            a.setUuid(s.getStateUuid());
            a.setFlatBuilNo(s.getFlatBuildingName());
            a.setLocality(s.getLocality());
            a.setPincode(s.getPincode());
            ASI.saveAddress(a,c);
            SaveAddressResponse aur=new SaveAddressResponse().id(a.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
            return new ResponseEntity<SaveAddressResponse>(aur, HttpStatus.OK);
    }
    @RequestMapping(value = "/address/customer",method= RequestMethod.GET)
    public ResponseEntity<AddressListResponse>getAllAddress(@RequestHeader("authorization")final String authentication)throws Exception{
        CustomerEntity c=CSI.getCustomer(authentication);
        if(c==null)
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        List<AddressEntity>list=ASI.getAllAddress(c);

        List<AddressList>l=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            AddressListState als=new AddressListState().id(UUID.fromString(list.get(i).getState().getUuid())).stateName(list.get(i).getState().getState_name());
            AddressList a=new AddressList().id(UUID.fromString(list.get(i).getUuid())).flatBuildingName(list.get(i).getFlatBuilNo()).city(list.get(i).getCity()).locality(list.get(i).getLocality()).pincode(list.get(i).getPincode()).state(als);
            l.add(a);
        }

        final AddressListResponse addressLists = new AddressListResponse().addresses(l);
        return new ResponseEntity<AddressListResponse>(addressLists, HttpStatus.OK);
    }
    @RequestMapping(value = "/address/{address_id}",method= RequestMethod.DELETE)
    public ResponseEntity<DeleteAddressResponse>deleteAddress(@PathVariable("address_id") String address_id,@RequestHeader("authorization")final String authentication)throws Exception{
        CustomerEntity c=CSI.getCustomer(authentication);
        AddressEntity ar=ad.getAddress(address_id);
        System.out.println("POPOPOPO"+address_id+" "+ar.getUuid());
        if(!address_id.equals(ar.getUuid()))
            throw new AuthorizationFailedException("ATHR-004","You are not authorized to view/update/delete any one else's address");

        ASI.deleteAddress(ar);
        DeleteAddressResponse aur=new DeleteAddressResponse().id(UUID.fromString(address_id)).status("ADDRESS DELETED SUCCESSFULLY");
        return new ResponseEntity<DeleteAddressResponse>(aur,HttpStatus.OK);
    }
}

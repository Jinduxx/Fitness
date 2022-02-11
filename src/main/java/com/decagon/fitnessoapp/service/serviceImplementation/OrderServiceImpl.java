package com.decagon.fitnessoapp.service.serviceImplementation;

import com.decagon.fitnessoapp.dto.OrderResponse;
import com.decagon.fitnessoapp.model.product.Cart;
import com.decagon.fitnessoapp.model.product.ORDER_STATUS;
import com.decagon.fitnessoapp.model.product.Order;
import com.decagon.fitnessoapp.model.user.Person;
import com.decagon.fitnessoapp.repository.OrderRepository;
import com.decagon.fitnessoapp.repository.PersonRepository;
import com.decagon.fitnessoapp.repository.ShoppingCartRepository;
import com.decagon.fitnessoapp.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, PersonRepository personRepository,
                            ModelMapper modelMapper, ShoppingCartRepository shoppingCartRepository) {
        this.orderRepository = orderRepository;
        this.personRepository = personRepository;
        this.modelMapper = modelMapper;
        this.shoppingCartRepository = shoppingCartRepository;
    }


    @Override
    public List<OrderResponse> getAllOrderByPerson(Authentication authentication) {

        Person person = personRepository.findPersonByUserName(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("Check getOrder at OrderServiceImpl: User Name does not Exist"));
        List<Order> orders = orderRepository.findAllByCheckOut_Person(person);
        List<OrderResponse> listOfOrders = new ArrayList<>();
        if(!orders.isEmpty()){
            for (Order orderlist : orders) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderDate(orderlist.getCheckOut().getOrderDate());
                orderResponse.setOrderStatus(orderlist.getOrderStatus());
                orderResponse.setBillingAddress(orderlist.getCheckOut().getBillingAddress());
                orderResponse.setCouponCode(orderlist.getCheckOut().getCouponCode());
                orderResponse.setEmail(orderlist.getCheckOut().getPerson().getEmail());
                orderResponse.setFirstName(orderlist.getCheckOut().getPerson().getFirstName());
                orderResponse.setLastName(orderlist.getCheckOut().getPerson().getLastName());
                orderResponse.setBillingAddress(orderlist.getCheckOut().getBillingAddress());
                orderResponse.setTotalPrice(orderlist.getCheckOut().getTotalPrice());
//                orderResponse.setPaymentCard(orderlist.getCheckOut().getPaymentCard());
                orderResponse.setReferenceNumber(orderlist.getCheckOut().getReferenceNumber());
                orderResponse.setShippingMethod(orderlist.getCheckOut().getShippingMethod());
                orderResponse.setTransactionStatus(orderlist.getCheckOut().getTransactionStatus());
                orderResponse.setCartList(shoppingCartRepository
                        .findAllByUniqueCartId(orderlist.getCheckOut().getShoppingCartUniqueId()));
                listOfOrders.add(orderResponse);
            }
        }
    }

//    @Override
//    public List<OrderResponse> getAllOrders(int pageSize) {
//        int pageNo = 0;
//        String sortBy = "id";
//        Pageable orderPage = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
//
//        List<OrderResponse> orderList = orderRepository.findAll(orderPage).getContent()

   /* @Override
    public Page<OrderResponse> getAllOrders(int pageNo) {
        int pageSize = 10;
        int skipCount = (pageNo - 1) * pageSize;

        List<OrderResponse> orderList = orderRepository.findAll()
                .stream()
                .map(x -> modelMapper.map(x.getCheckOut(), OrderResponse.class))
                .collect(Collectors.toList());
//                .stream()
//                .map(x -> modelMapper.map(x.getCheckOut(), OrderResponse.class))
//                .collect(Collectors.toList());
//            orderList
//                    .forEach(y -> y.setCartList(shoppingCartRepository
//                            .findAllByUniqueCartId(y.getShoppingCartUniqueId())));
//        return orderList;
//    }

        Pageable orderPage = PageRequest.of(pageNo, pageSize, Sort.by("productName").ascending());
        System.out.println(orderList);

        return new PageImpl<>(orderList, orderPage, orderList.size());
    }*/

    @Override
    public List<OrderResponse> getAlOrders(Integer pageNo) {
        int pageSize = 10;
        String sortBy = "id";
        Pageable orderPage = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        Page<Order> pagedOrders = orderRepository.findAll(orderPage);
        List<OrderResponse> listOfOrders = new ArrayList<>();
        if(pagedOrders.hasContent()){
            for (Order order : pagedOrders.getContent()) {
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderDate(order.getCheckOut().getOrderDate());
                orderResponse.setOrderStatus(order.getOrderStatus());
                orderResponse.setBillingAddress(order.getCheckOut().getBillingAddress());
                orderResponse.setCouponCode(order.getCheckOut().getCouponCode());
                orderResponse.setEmail(order.getCheckOut().getPerson().getEmail());
                orderResponse.setFirstName(order.getCheckOut().getPerson().getFirstName());
                orderResponse.setLastName(order.getCheckOut().getPerson().getLastName());
                orderResponse.setBillingAddress(order.getCheckOut().getBillingAddress());
                orderResponse.setTotalPrice(order.getCheckOut().getTotalPrice());
//                orderResponse.setPaymentCard(order.getCheckOut().getPaymentCard());
                orderResponse.setReferenceNumber(order.getCheckOut().getReferenceNumber());
                orderResponse.setShippingMethod(order.getCheckOut().getShippingMethod());
                orderResponse.setTransactionStatus(order.getCheckOut().getTransactionStatus());
                orderResponse.setCartList(shoppingCartRepository
                        .findAllByUniqueCartId(order.getCheckOut().getShoppingCartUniqueId()));
                listOfOrders.add(orderResponse);
            }
        }
        return listOfOrders;

    }

    @Override
    public Page<OrderResponse> getOrdersByStatus(ORDER_STATUS status, int pageNo) {
        int pageSize = 10;
        int skipCount = (pageNo - 1) * pageSize;

        List<OrderResponse> orderList = orderRepository.findAllByOrderStatus(status)
                .stream()
                .map(x -> modelMapper.map(x, OrderResponse.class))
                .collect(Collectors.toList())
                .stream()
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());

        Pageable orderPage = PageRequest.of(pageNo, pageSize, Sort.by("productName").ascending());

        return new PageImpl<>(orderList, orderPage, orderList.size());
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Invalid order Id"));
        order.setOrderStatus(ORDER_STATUS.CANCELLED);
        orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }
    @Override
    public OrderResponse completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Invalid order Id"));
        order.setOrderStatus(ORDER_STATUS.COMPLETED);
        orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }
}

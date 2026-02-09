# TODO
- [ ] 관리자 기능 역할 기반 접근 제어 (RBAC)
- [ ] Book 생성 시 type을 강제 고정
- [ ] OrderItemStatusHistory 엔티티 추가 (반품조건, CS 등 이유)
- [ ] 반품기능 구현
- [ ] Spring 상태머신 도입

# Problem
- [x] 결제하면 데드락 발생
- [ ] Spring Security 때문에 모든 에러가 403
- [ ] @TransactionalEventListener(phase = AFTER_COMMIT)으로 호출하는 메서드의 @Transactional 동작안함

# Solved
- 결제하면 데드락 발생
  - 원인: React 개발모드의 <React.StrictMode>로 2개의 동시 요청. 
    paymentService.completePaymentConfirm의 트랜잭션이 너무 길게 잡혀있어서 
    두 트랜잭션이 동시에 order에 대해 s-lock 걸고 x-lock 걸려고 시도해서 데드락 발생
  - 해결:
  1. paymentService.completePaymentConfirm에 @Transcational 제거
  2. PaymentFacadeService로 completePaymentConfirm 옮기고 orderService.updateStatus 추가
  3. completePaymentConfirm에 짧은 트랜잭션 3개로 데드락 해결
package com.spark

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class LottoController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Lotto.list(params), model:[lottoCount: Lotto.count()]
    }

    def show(Lotto lotto) {
        respond lotto
    }

    def create() {
        respond new Lotto(params)
    }

    @Transactional
    def save(Lotto lotto) {
        if (lotto == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (lotto.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond lotto.errors, view:'create'
            return
        }

        lotto.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'lotto.label', default: 'Lotto'), lotto.id])
                redirect lotto
            }
            '*' { respond lotto, [status: CREATED] }
        }
    }

    def edit(Lotto lotto) {
        respond lotto
    }

    @Transactional
    def update(Lotto lotto) {
        if (lotto == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (lotto.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond lotto.errors, view:'edit'
            return
        }

        lotto.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'lotto.label', default: 'Lotto'), lotto.id])
                redirect lotto
            }
            '*'{ respond lotto, [status: OK] }
        }
    }

    @Transactional
    def delete(Lotto lotto) {

        if (lotto == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        lotto.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'lotto.label', default: 'Lotto'), lotto.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'lotto.label', default: 'Lotto'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

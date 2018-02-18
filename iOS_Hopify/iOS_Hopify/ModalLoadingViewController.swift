//
//  ModalViewController.swift
//  iOS_Hopify
//
//  Created by Bart Chrzaszcz on 2018-02-17.
//  Copyright © 2018 Bart Chrzaszcz. All rights reserved.
//

import UIKit
import NVActivityIndicatorView

class ModalLoadingViewController: UIViewController {

    var modalView = UIView(frame: CGRect(x: 0, y: 0, width: 150, height: 150))
    var activityIndicatorView = NVActivityIndicatorView(frame: CGRect(x: 35, y: 35, width: 200, height: 200), type: .pacman, color: .yellow, padding: 5)

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        modalView.backgroundColor = .clear
        modalView.center = CGPoint(x: view.frame.size.width  / 2 - 50, y: view.frame.size.height / 2 - 50);
        activityIndicatorView.frame = CGRect(x: modalView.frame.size.width  / 2 - 30, y: modalView.frame.size.height / 2 - 40, width: 200, height: 200)
        modalView.frame.size = CGSize(width: 250, height: 250)
        view.addSubview(modalView)
        modalView.addSubview(activityIndicatorView)
        activityIndicatorView.startAnimating()
        view.backgroundColor = .clear
    }
}

/*class ModalPhoneViewController: UIViewController {

    var modalView = UIView(frame: CGRect(x: 0, y: 0, width: 150, height: 150))
    var phoneInputView = UITextField(frame: CGRect(x: 35, y: 35, width: 100, height: 50))

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        modalView.backgroundColor = .clear

        modalView.center = CGPoint(x: view.frame.size.width  / 2 - 50, y: view.frame.size.height / 2 - 50);
        phoneInputView.frame = CGRect(x: modalView.frame.size.width  / 2 - 30, y: modalView.frame.size.height / 2 - 40, width: 250, height: 50)
        modalView.frame.size = CGSize(width: 250, height: 250)
        phoneInputView.backgroundColor = .red
        phoneInputView.borderStyle = .bezel

        view.addSubview(modalView)

        phoneInputView.keyboardType = .numberPad
        modalView.addSubview(phoneInputView)
        view.backgroundColor = .clear
    }
}*/

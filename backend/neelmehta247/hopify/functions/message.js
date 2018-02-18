const lib = require('lib')({token: process.env.stdlib_library_token});

/**
 * An endpoint to send messages to necessary users
 * @param {string} url The deeplink URL to send
 * @param {string} number The numbers to send messages to
 * @returns {any}
 */
module.exports = async (url, number, context) => {
    return await lib.utils.sms({
        to: number,
        body: `Your friend shared their CityCrawler plan with you. Check it out at ${url}.`
    });

    /*
    let smsPromises = numbers.map(number => {
        return lib.utils.sms({
            to: number,
            body: `Your friend shared their CityCrawler plan with you. Check it out at ${url}.`
        });
        // return lib.messagebird.sms({
        //     recipient: number,
        //     body: `Your friend shared their CityCrawler plan with you. Check it out at ${url}.`
        // });
    });

    const result = await Promise.all(smsPromises).catch(err => {
      throw err
    });

    return result;
*/


    // await numbers.forEach(async (number) => {
    //     const pn = new PhoneNumber(number);
    //     if (pn.isValid() && pn.isMobile() && pn.getRegionCode() === 'US') {
    //         await lib({bg: true}).messagebird.sms({
    //             recipient: pn.getNumber('e164'),
    //             body: `Your friend shared their CityCrawler plan with you. Check it out at ${url}.`
    //         });
    //     } else {
    //         console.log("Message not sent to: " + number);
    //     }

    //     return;
    // });

    // return {success: true};
}
